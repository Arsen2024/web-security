
package com.example.carmanagementsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder
    ) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails superuser = User.builder()
                .username("superuser")
                .password(passwordEncoder.encode("super123"))
                .roles("SUPERUSER")
                .build();

        return new InMemoryUserDetailsManager(
                user,
                admin,
                superuser
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) {
        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                HttpMethod.GET,
                                "/cars",
                                "/cars/**"
                        ).hasAnyRole(
                                "USER",
                                "ADMIN",
                                "SUPERUSER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/cars"
                        ).hasAnyRole(
                                "ADMIN",
                                "SUPERUSER"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/cars/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SUPERUSER"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/cars/**"
                        ).hasRole("SUPERUSER")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
