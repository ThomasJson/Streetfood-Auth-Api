package fr.tp.utils;

import fr.tp.entities.AccountEntity;
import fr.tp.models.AuthResponseModel;
import io.smallrye.jwt.build.Jwt;
import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;

public class AuthUtils {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public static String encodePassword(String passwordClean){
        return BCrypt.hashpw(passwordClean,BCrypt.gensalt(9));
    }

    public static boolean checkPassword(String passwordClean, String storedHash) {
        try {
            return BCrypt.checkpw(passwordClean, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    public static AuthResponseModel generateAuthResponse(AccountEntity account) {

        String token = Jwt.issuer("StreetF")
                .upn(account.getMail())
                .claim("id", account.getId())
                .claim("mail", account.getMail())
                .claim("role", account.getRole())
                .expiresAt(System.currentTimeMillis()+3600*10)
                .sign();

        return new AuthResponseModel("Bearer " + token, account.getId(), account.getRole().getWeight(), true);
    }

}