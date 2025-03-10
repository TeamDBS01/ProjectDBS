package com.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable) // Turn off CSRF (not needed for API)
                .cors(Customizer.withDefaults()) // Enable CORS (to allow requests from other domains)
                // Secure different parts of the app
                .authorizeHttpRequests(request -> request.requestMatchers("/auth/**", "/public/**").permitAll() // Public Paths
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN") // Admin-only paths
                        .requestMatchers("/user/**").hasAnyAuthority("USER") // User-only paths
                        .requestMatchers("/adminuser/**").hasAnyAuthority("ADMIN", "USER") // Both role can access
                        .anyRequest().authenticated()) // All other paths need login
                // Don't store the sessions on the server (stateless)
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Use a Custom authentication provider
                .authenticationProvider(authenticationProvider())
                // Add a custom JWT filter to check tokens
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(); // Create a new DaoAuthenticationProvider
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService); // Links the custom userDetailsService
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); // Set the passwordEncoder for secure password verification
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
