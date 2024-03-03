package fr.tp.models;

public class ConfirmMail {

    private String email;

    private String subject;

    private String body;

    public ConfirmMail(String email, String subject, String confirmationLink) {
        this.email = email;
        this.subject = subject;
        this.body = confirmationLink;
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
