package ua.kyiv.palace.ejournal.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ua.kyiv.palace.ejournal.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/css/**", "/images/**").permitAll() // дозволити доступ до логіну і ресурсів
                .antMatchers("/owner/**").hasRole("OWNER") // доступ тільки для OWNER
                .antMatchers("/admin/**").hasRole("ADMIN") // доступ тільки для ADMIN
                .antMatchers("/teacher/**").hasRole("TEACHER") // доступ тільки для TEACHER
                .anyRequest().authenticated() // інші запити потребують авторизації
                .and()
                .formLogin()
                .loginPage("/login") // сторінка логіну
                .loginProcessingUrl("/login") // обробка форми
                .usernameParameter("username") // параметр для email
                .passwordParameter("password") // параметр для пароля
                .defaultSuccessUrl("/", true) // перенаправлення після успішного логіну (по ролі буде змінюватися)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // URL для виведення
                .logoutSuccessUrl("/login?logout") // перенаправлення після виходу
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403"); // сторінка доступу відмовлено

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService) // налаштування userDetailsService
                .passwordEncoder(passwordEncoder()) // налаштування паролів
                .and().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // алгоритм для хешування паролів
    }
}
