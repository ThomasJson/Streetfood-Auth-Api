package fr.tp.repositories;

import fr.tp.entities.RoleEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<RoleEntity> {
    public Optional<RoleEntity> findRoleByTitle(String title) {
        return find("title", title).firstResultOptional();
    }

    public RoleEntity createRole(String title, int weight) {
        RoleEntity role = new RoleEntity();
        role.setTitle(title);
        role.setWeight(weight);
        persist(role);
        return role;
    }
}
