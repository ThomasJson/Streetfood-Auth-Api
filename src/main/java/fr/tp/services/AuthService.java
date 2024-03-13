package fr.tp.services;

import fr.tp.entities.AccountEntity;
import fr.tp.entities.AppUserEntity;
import fr.tp.entities.RoleEntity;
import fr.tp.models.LoginResponse;
import fr.tp.repositories.AccountRepository;
import fr.tp.repositories.AppUserRepository;
import fr.tp.repositories.RoleRepository;
import fr.tp.utils.AuthUtils;
import fr.tp.utils.TokenUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.resource.spi.ConfigProperty;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    AppUserRepository appUserRepository;

    @Inject
    JwtService jwtService;


    public LoginResponse authenticate(String mail, String password) throws Exception {

        Optional<AccountEntity> accountOpt = accountRepository.findByMail(mail);
        boolean isPassword = AuthUtils.checkPassword(password, accountOpt.get().getPassword());

        if (accountOpt.isPresent() && isPassword) {

            AccountEntity acc = accountOpt.get();
            String token = JwtService.generateJwt(acc);
            return new LoginResponse("Bearer " + token, acc.getId(), acc.getRole().getWeight(), true);

        }

        throw new SecurityException("Invalid credentials provided.");
    }

    @Transactional
    public AccountEntity create(String mail, String password, String firstName, String pseudo) {

        Optional<RoleEntity> roleUser = roleRepository.findRoleByTitle("User");
        AppUserEntity newAppUser = appUserRepository.createNewAppUser(firstName);
        Optional<AppUserEntity> appUser = appUserRepository.findAppUserByFirstName(firstName);

        if (roleUser.isPresent() && appUser.isPresent()) {
            AccountEntity newAccount = new AccountEntity();

            String pswHashed = AuthUtils.encodePassword(password);

            newAccount.setMail(mail);
            newAccount.setPassword(pswHashed);
            newAccount.setPseudo(pseudo);
            newAccount.setAppUser(newAppUser);
            newAccount.setRole(roleUser.get());

            accountRepository.persist(newAccount);
            return newAccount;

        } else {
            throw new IllegalStateException("Role or AppUser not found");
        }

    }

    public Optional<AccountEntity> check(String mail){
        Optional<AccountEntity> accountOpt = accountRepository.findByMail(mail);
        if(accountOpt.isPresent()){
            return accountOpt;
        }
        else {
            throw new IllegalStateException("Account not found");
        }
    }

}