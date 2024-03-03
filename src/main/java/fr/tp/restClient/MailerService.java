package fr.tp.restClient;

import fr.tp.models.ConfirmMail;
import io.smallrye.common.annotation.Blocking;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/send")
@RegisterRestClient(configKey = "mail-api")
public interface MailerService {

    @POST
    @Blocking
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    Response sendEmail(ConfirmMail confirmMail);

}