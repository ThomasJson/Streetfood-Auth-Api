package fr.tp.models;

public class ConfirmMailModel {

    private String email;

    private String subject;

    private String body;

    public ConfirmMailModel(String email, String subject, String confirmationLink) {
        this.email = email;
        this.subject = subject;
        this.body =
            "<html>" +
                "<body>" +
                    "<p>To complete your registration, please click the link below:</p>" +
                    "<a href=\"" + confirmationLink + "\">Confirm Registration</a>" +
                "</body>" +
            "</html>";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
