package ua.kyiv.palace.ejournal.service;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.kyiv.palace.ejournal.repository.UserRepository;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Замість @Autowired використовуємо конструктор для передачі залежностей
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Отримуємо користувача за email
        ua.kyiv.palace.ejournal.model.User user = (ua.kyiv.palace.ejournal.model.User) userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Створюємо список прав доступу (SimpleGrantedAuthority)
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Повертаємо користувача з відповідними правами доступу
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),         // email
                user.getPassword(),      // password
                authorities              // роль (наприклад: ROLE_ADMIN, ROLE_OWNER)
        );
    }
}



