package fr.tp.services;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class JwtService {

    public String generateJwt(){

        Set<String> roles = new HashSet<>(
                List.of("Admin, User")
        );

        return Jwt.issuer("StreetFoodJwt")
                .subject("StreetFoodJwt")
                .groups(roles)
                .expiresAt(
                        System.currentTimeMillis() + 3600
                )
                .sign();
    }

}
