package ejournal.ejournal.util;

import ejournal.ejournal.model.RoleEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.RoleRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set; // ✅ Цей import більше не потрібен

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder pe;

    @Override
    public void run(String... args) {
        var rAdmin = roleRepo.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepo.save(RoleEntity.builder().name("ROLE_ADMIN").build()));
        roleRepo.findByName("ROLE_HEAD")
                .orElseGet(() -> roleRepo.save(RoleEntity.builder().name("ROLE_HEAD").build()));
        roleRepo.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepo.save(RoleEntity.builder().name("ROLE_TEACHER").build()));

        userRepo.findByUsername("admin").orElseGet(() ->
                userRepo.save(UserEntity.builder()
                        .username("admin") // технічне
                        .email("admin@ejournal.local")
                        .name("System")
                        .surname("Administrator")
                        .password(pe.encode("admin123"))
                        // ✅ ЗМІНА: Використовуємо .role() замість .roles()
                        .role(rAdmin)
                        .enabled(true)
                        .build()));
    }
}