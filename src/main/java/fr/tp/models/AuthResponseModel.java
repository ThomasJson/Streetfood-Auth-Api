package fr.tp.models;

import java.util.UUID;

public class AuthResponseModel {
    private String token;
    private UUID accountId;
    private int roleWeight;
    private boolean result;

    public AuthResponseModel(String token, UUID accountId, int roleWeight, boolean result) {
        this.token = token;
        this.accountId = accountId;
        this.roleWeight = roleWeight;
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public int getRoleWeight() {
        return roleWeight;
    }

    public void setRoleWeight(int roleWeight) {
        this.roleWeight = roleWeight;
    }
    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
