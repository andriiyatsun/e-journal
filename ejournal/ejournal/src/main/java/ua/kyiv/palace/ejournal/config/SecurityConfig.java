package ua.kyiv.palace.ejournal.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Дозволити всі запити без авторизації
                )
                .csrf(csrf -> csrf.disable()) // Відключити CSRF
                .formLogin(form -> form.disable()) // Відключити form login
                .httpBasic(basic -> basic.disable()) // Відключити basic auth
                .logout(logout -> logout.disable()); // Відключити logout

        return http.build();
    }
}