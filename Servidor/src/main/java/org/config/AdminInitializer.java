package org.config; // O donde esté tu clase

import java.util.Set;
import org.model.accounts.Account;
import org.model.enums.Role;
import org.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Value; // <-- Importante
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${ADMIN_USERNAME}")
  private String adminUsername;

  @Value("${ADMIN_PASSWORD}")
  private String adminPassword;

  public AdminInitializer(AccountRepository accountRepository,
                          PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    if (!accountRepository.existsByUsername(adminUsername)) {
      try {
        Account admin = new Account();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
  admin.setRoles(Set.of(Role.ROLE_ADMIN));

        accountRepository.save(admin);
        System.out.println("Usuario administrador '" + adminUsername + "' creado.");

      } catch (DuplicateKeyException e) {
        System.out.println("Otro nodo ya creó el usuario administrador.");
      }
    } else {
      System.out.println("Ya existe el usuario administrador, no se crea uno nuevo.");
    }
  }
}