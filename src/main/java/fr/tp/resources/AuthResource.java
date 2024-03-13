package fr.tp.resources;

import fr.tp.entities.AccountEntity;
import fr.tp.models.*;
import fr.tp.repositories.AccountRepository;
import fr.tp.restClient.MailerService;

import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import io.smallrye.jwt.auth.principal.JWTParser;

import fr.tp.services.AuthService;
import fr.tp.utils.AuthUtils;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Context
    UriBuilder uriBuilder;

    @Context
    UriInfo uriInfo;

    @Inject JWTParser parser;

    @Inject
    AuthService authService;

    @Inject
    @RestClient
    MailerService mailerService;

    @Inject
    AccountRepository accountRepository;

    @POST
    @PermitAll
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginInput) {

        try {

            LoginResponse loginResponse = authService.authenticate(loginInput.getMail(), loginInput.getPassword());
            return Response.ok().entity(loginResponse).build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("Unable to process request.", false))
                    .build();
        }

        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("An unexpected error occurred.", false))
                    .build();
        }
    }

    @POST
    @PermitAll
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest registerInput) {
        try {

            if (!AuthUtils.isValidEmail(registerInput.getMail())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new AuthResponse("Invalid email adress.", false))
                        .build();
            }

            if (accountRepository.findByMail(registerInput.getMail()).isPresent()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new AuthResponse("Email adress already used.", false))
                        .build();
            }

            String confirmationToken = Jwt.issuer("ConfirmEmailToken")
                    .upn(registerInput.getMail())
                    .claim("mail", registerInput.getMail())
                    .claim("pseudo", registerInput.getPseudo())
                    .claim("firstName", registerInput.getFirstName())
                    .expiresAt(System.currentTimeMillis() + 600000)
                    .sign();

            String confirmationLink = "http://localhost:3000/account/validate/" + confirmationToken;
            ConfirmMail confirmMail = new ConfirmMail(registerInput.getMail(), "Create your new Streetfood.com account", confirmationLink);
            mailerService.sendEmail(confirmMail);

            return Response.status(Response.Status.OK)
                    .entity(new AuthResponse("Email successfully sent.", true))
                    .build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("Unable to process request.", false))
                    .build();
        }

        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("An unexpected error occurred.", false))
                    .build();
        }
    }

    @POST
    @PermitAll
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(TokenRequest tokenRequest) {

        try {
            JsonWebToken jwt = parser.parse(tokenRequest.getToken());

            if (jwt != null && jwt.getIssuer().equals("ConfirmEmailToken")) {

                return Response.ok().entity("{\"token\":\"" + tokenRequest.getToken() + "\"}").build();
            } else {

                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new AuthResponse("Invalid token.", false)).build();
            }
        } catch (JwtException | ParseException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new AuthResponse("Token validation error.", false)).build();
        }

        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("An unexpected error occurred.", false))
                    .build();
        }
    }

    @POST
    @PermitAll
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CreateUserRequest createUserRequest) {

        try {
            JsonWebToken jwt = parser.parse(createUserRequest.getToken());
            if (jwt != null) {

                String mail = jwt.getClaim("mail");
                String pseudo = jwt.getClaim("pseudo");
                String firstName = jwt.getClaim("firstName");
                String password = createUserRequest.getPassword();

                AccountEntity acc = authService.create(mail, password, firstName, pseudo);

                if (acc != null) {
                    return Response.status(Response.Status.OK)
                            .entity(new AuthResponse("Account created :)", true))
                            .build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new AuthResponse("Error creating the account.", false))
                            .build();
                }
            } else{
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new AuthResponse("Token is null.", false))
                        .build();
            }
        } catch (JwtException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new AuthResponse("Invalid token.", false))
                    .build();

        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new AuthResponse("Token parsing error.", false))
                    .build();
        }

        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("An unexpected error occurred.", false))
                    .build();
        }
    }

    @GET
    @RolesAllowed({"Admin","User"})
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCookie(@CookieParam("StreetF") String cookie) {

        if (cookie == null || cookie.isEmpty()) {
            return Response.status(Response.Status.OK)
                    .entity(new AuthResponse("Null or empty token.", false))
                    .build();
        }

        try {
            String token = cookie.startsWith("Bearer ") ? cookie.substring(7) : cookie;
            JsonWebToken jwt = parser.parse(token);

            if (!jwt.containsClaim("mail")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new AuthResponse("Token is missing some data.", false))
                        .build();
            }

            String email = jwt.getClaim("mail");
            Optional<AccountEntity> accountOpt = authService.check(email);

            if (accountOpt.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new AuthResponse("Account not found.", false))
                        .build();
            }

            AccountEntity account = accountOpt.get();
            LoginResponse loginResponse = new LoginResponse(token, account.getId(), account.getRole().getWeight(), true);
            return Response.ok().entity(loginResponse).build();

        } catch (JwtException | ParseException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new AuthResponse("Token validation error.", false))
                    .build();
        }

        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse("An unexpected error occurred.", false))
                    .build();
        }
    }

}