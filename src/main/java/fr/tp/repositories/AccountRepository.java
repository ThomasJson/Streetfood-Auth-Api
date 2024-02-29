package fr.tp.repositories;

import fr.tp.entities.AccountEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<AccountEntity> {

    public Optional<AccountEntity> findById(UUID id) {
        return Optional.ofNullable(find("id", id).firstResult());
    }

    public Optional<AccountEntity> findByMail(String mail) {
        return find("mail", mail).firstResultOptional();
    }

}