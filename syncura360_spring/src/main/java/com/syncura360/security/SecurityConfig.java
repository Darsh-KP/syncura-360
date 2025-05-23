package com.syncura360.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // User Auth
                        .requestMatchers("/", "/register/**", "/login").permitAll()

                        .requestMatchers("/record/**").hasAnyAuthority("Admin", "Super Admin", "Doctor", "Nurse")

                        .requestMatchers("/visit/**").hasAnyAuthority("Doctor", "Nurse")

                        .requestMatchers("/schedule/staff").hasAnyAuthority("Admin", "Super Admin", "Doctor", "Nurse")
                        .requestMatchers("/staff/**", "/test_auth", "/staff", "/schedule/**", "/schedule", "/drug", "/service/**", "/service").hasAnyAuthority("Admin", "Super Admin")

                        // Room
                        .requestMatchers(HttpMethod.GET, "/room").hasAnyAuthority("Doctor", "Nurse", "Admin", "Super Admin")
                        .requestMatchers("/room").hasAnyAuthority("Admin", "Super Admin")

                        // Patient
                        .requestMatchers("/patient", "/patient/{patient-id}").hasAnyAuthority("Doctor", "Nurse")

                        // Account settings
                        .requestMatchers("/setting/hospital", "/setting/staff", "setting/password").hasAnyAuthority("Super Admin", "Admin", "Doctor", "Nurse")

                        // Auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            StaffDetailsService staffDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(staffDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordSecurity.getPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Allow all origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Allow specific HTTP methods
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS to all endpoints

        return source;
    }
}