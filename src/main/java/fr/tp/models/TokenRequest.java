package fr.tp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequest {

    @JsonProperty("token")
    private String token;

    public TokenRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
