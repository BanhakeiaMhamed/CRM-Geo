package com.backend.backend.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final FirebaseProperties firebaseProperties;

    public SecurityConfig(FirebaseAuthenticationFilter firebaseAuthenticationFilter,
                          CorsConfigurationSource corsConfigurationSource,
                          FirebaseProperties firebaseProperties) {
        this.firebaseAuthenticationFilter = firebaseAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (firebaseProperties.isEnabled()) {
            http.authorizeHttpRequests(request ->
                    request.requestMatchers("/public/**").permitAll()
                            .requestMatchers("/api/**").authenticated()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/user/**").hasAnyRole("ADMIN", "USER", "INGENIEUR", "GERANT")
                            .anyRequest().authenticated());
        } else {
            http.authorizeHttpRequests(request -> request.anyRequest().permitAll());
        }

        return http.build();
    }
}
