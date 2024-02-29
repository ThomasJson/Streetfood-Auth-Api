package fr.tp.repositories;

import fr.tp.entities.AppUserEntity;
import fr.tp.entities.RoleEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AppUserRepository implements PanacheRepository<AppUserEntity> {
    public AppUserEntity createNewAppUser(String firstName) {

        AppUserEntity appUser = new AppUserEntity();
        appUser.setFirstName(firstName);
        persist(appUser);
        return appUser;
    }

    public Optional<AppUserEntity> findAppUserByFirstName(String firstName) {
        return find("firstName", firstName).firstResultOptional();
    }
}
