package fr.tp.resources;

import fr.tp.entities.AccountEntity;
import fr.tp.models.*;
import fr.tp.repositories.AccountRepository;
import fr.tp.restClient.MailerService;

import io.smallrye.jwt.auth.principal.ParseException;
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

    private CurrentUserModel currentUser;

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
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginInputModel loginInput) {

        try {
            AuthResponseModel authResponseModel = authService.authenticate(loginInput.getMail(), loginInput.getPassword());
            return Response.ok().entity(authResponseModel).build();
        }

        catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterInputModel registerInput) {

        try {

            AccountRepository accountRepository = new AccountRepository();

            if (!AuthUtils.isValidEmail(registerInput.getMail())) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Email invalide.").build();
            } else if (accountRepository.findByMail(registerInput.getMail()).isPresent()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Email déjà utilisé.").build();
            }

            String confirmationToken = Jwt.issuer("ConfirmEmailToken")
                    .upn(registerInput.getMail())
                    .claim("mail", registerInput.getMail())
                    .claim("firstName", registerInput.getFirstName())
                    .expiresAt(System.currentTimeMillis()+600) // 10min
                    .sign();

            currentUser = new CurrentUserModel(registerInput.getMail(), registerInput.getPassword(), registerInput.getFirstName(), registerInput.getPseudo());

            UriBuilder builder = uriInfo.getBaseUriBuilder()
                    .path("auth/confirm")
                    .queryParam("token", confirmationToken);

            String confirmationLink = builder.build().toString();

            ConfirmMailModel confirmMail = new ConfirmMailModel(registerInput.getMail(), "Create Account", confirmationLink);
            mailerService.sendEmail(confirmMail);

            return Response.status(Response.Status.OK).entity("Un email de confirmation a été envoyé.").build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/confirm")
    public Response confirm(@QueryParam("token") String token) {

        try {
            JsonWebToken jwt = parser.parse(token);

            if(jwt.containsClaim("mail") && jwt.getClaim("mail").equals(currentUser.getCurrentMail())) {

                AccountEntity account = authService.register(currentUser.getCurrentMail(), currentUser.getCurrentPsw(), currentUser.getCurrentName(), currentUser.getCurrentPseudo());

                if (account != null) {
                    return Response.status(Response.Status.OK).entity("Le compte a bien été créé.").build();
                }
                else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Erreur lors de la création du compte.").build();
                }

            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Token invalide.").build();
            }

        } catch (JwtException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token invalide.").build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO : make checkCookie better ! Create authService.check() (no repository in this. !)

    @GET
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCookie(@CookieParam("StreetF") String cookie) {

        if (cookie != null) {

            try {
                String token = cookie.startsWith("Bearer ") ? cookie.substring(7) : cookie;
                JsonWebToken jwt = parser.parse(token);

                if(jwt.containsClaim("mail")) {

                    String email = jwt.getClaim("mail");
                    Optional<AccountEntity> account = authService.check(email);
                    AuthResponseModel authResponseModel = new AuthResponseModel(cookie, account.get().getId(), 4, true);
                    return Response.ok().entity(authResponseModel).build();
                }

            } catch (ParseException e) {
                return null;
            }

            return null;

        } else {
            return Response.ok(new AuthResponseModel(null, null, 0, false)).build();
        }
    }

}