package fr.tp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateUserRequest {
    @JsonProperty("token")
    private String token;
    @JsonProperty("password")
    private String password;

    @JsonProperty("confirm")
    private String confirm;

    public CreateUserRequest(String token, String password, String confirm) {
        this.token = token;
        this.password = password;
        this.confirm = confirm;
    }

    public CreateUserRequest(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
