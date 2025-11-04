package ejournal.ejournal.config;

import ejournal.ejournal.model.RoleEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.RoleRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// import java.util.Set; // Більше не потрібен

/**
 * Цей клас автоматично виконається при старті програми
 * і додасть в базу даних початкові дані (ролі та адміна),
 * якщо їх там ще немає.
 */
@Component
@RequiredArgsConstructor // Використовуємо ін'єкцію через конструктор
public class DataInitializer {

    // Поля будуть автоматично заповнені через @RequiredArgsConstructor
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Цей @Bean створює CommandLineRunner, який є "завданням",
     * що виконується один раз після завантаження контексту програми.
     *
     * @return завдання для виконання
     */
    @Bean
    public CommandLineRunner initData() {
        // Тепер не потрібні аргументи, оскільки поля вже ініціалізовані
        return args -> {

            // --- 1. Створення ролей ---
            // Спробуємо знайти роль, і якщо її немає, створимо і збережемо
            RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(RoleEntity.builder().name("ROLE_ADMIN").build()));

            roleRepository.findByName("ROLE_HEAD")
                    .orElseGet(() -> roleRepository.save(RoleEntity.builder().name("ROLE_HEAD").build()));

            roleRepository.findByName("ROLE_TEACHER")
                    .orElseGet(() -> roleRepository.save(RoleEntity.builder().name("ROLE_TEACHER").build()));

            // --- 2. Створення адміна ---

            // Шукаємо по "admin@ejournal.com" (новий логін)
            if (userRepository.findByEmail("admin@ejournal.com").isEmpty()) {

                // Якщо ні, створюємо нового
                UserEntity adminUser = UserEntity.builder()
                        .email("admin@ejournal.com") // Це буде логін
                        // Використовуємо email як username для уникнення проблем з унікальністю
                        .username("admin@ejournal.com")
                        .name("Admin")
                        .surname("Adminov")
                        // ВАЖЛИВО: Хешуємо пароль "admin123"
                        .password(passwordEncoder.encode("admin123"))
                        .enabled(true)
                        // ✅ ЗМІНА: Призначаємо одну роль через .role()
                        .role(adminRole)
                        .build();

                // Зберігаємо адміна в базу
                userRepository.save(adminUser);

                // Виводимо в консоль, щоб бачити, що адмін створений
                System.out.println(">>> Default admin user created! (login: admin@ejournal.com, pass: admin123)");
            }
        };
    }
}
