package com.hotel.reservas.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.hotel.reservas.login.security.JwtAuthenticationEntryPoint;
import com.hotel.reservas.login.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // PÚBLICAS - Sin autenticación
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-resources/**", "/swagger-ui/index.html").permitAll()
                .requestMatchers("/api/auth/**").permitAll() // Login y registro
                .requestMatchers("/api/public/**").permitAll() // Endpoints públicos
                .requestMatchers("/api/habitaciones/disponibles/**").permitAll() // Ver habitaciones disponibles
                
                // SOLO ADMIN - Dueño del hotel
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/clientes").hasRole("ADMIN") // Solo admin ve todos los clientes
                .requestMatchers("/api/clientes/{id}").hasRole("ADMIN") // Solo admin ve cliente específico
                .requestMatchers("/api/empleados/**").hasRole("ADMIN") // Solo admin maneja empleados
                .requestMatchers("/api/habitaciones/admin/**").hasRole("ADMIN") // Gestión de habitaciones
                
                // CLIENTE AUTENTICADO - Sus propios datos
                .requestMatchers("/api/clientes/mi-perfil").hasRole("CLIENTE")
                .requestMatchers("/api/clientes/mi-perfil/actualizar").hasRole("CLIENTE")
                .requestMatchers("/api/reservas/mis-reservas").hasRole("CLIENTE")
                .requestMatchers("/api/reservas/crear").hasRole("CLIENTE")
                .requestMatchers("/api/reservas/cancelar/**").hasRole("CLIENTE")
                
                // ADMIN Y CLIENTE - Según contexto
                .requestMatchers("/api/reservas/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/api/pagos/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/api/facturas/**").hasAnyRole("ADMIN", "CLIENTE")
                
                // TODO LO DEMÁS REQUIERE AUTENTICACIÓN
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}