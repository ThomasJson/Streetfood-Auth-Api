package fr.tp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginInputModel {
    @JsonProperty("mail")
    private String mail;

    @JsonProperty("password")
    private String password;

    public LoginInputModel() {
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
