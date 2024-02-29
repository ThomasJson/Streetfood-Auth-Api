package fr.tp.models;

public class CurrentUserModel {
    private String currentMail;
    private String currentPsw;
    private String currentName;
    private String currentPseudo;

    public CurrentUserModel(String mail, String password, String firstName, String pseudo){

        currentMail = mail;
        currentPsw = password;
        currentName = firstName;
        currentPseudo = pseudo;

    }

    public String getCurrentMail() {
        return currentMail;
    }
    public void setCurrentMail(String mail) {
        this.currentMail = mail;
    }
    public String getCurrentPsw() {
        return currentPsw;
    }
    public void setCurrentPsw(String currentPsw) {
        this.currentPsw = currentPsw;
    }
    public String getCurrentName() {
        return currentName;
    }
    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }
    public String getCurrentPseudo() {
        return currentPseudo;
    }
    public void setCurrentPseudo(String pseudo) {
        this.currentPseudo = pseudo;
    }

}
