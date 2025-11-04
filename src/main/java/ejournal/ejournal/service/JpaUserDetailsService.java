package ejournal.ejournal.service;

import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    // @Transactional(readOnly = true) // Більше не потрібен через FetchType.EAGER, але можна залишити
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = Collections.emptyList();

        // Перевіряємо, чи є у користувача взагалі роль
        if (user.getRole() != null) {
            // Якщо роль існує, створюємо список з ОДНІЄЇ ролі
            authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true, true, authorities
        );
    }
}