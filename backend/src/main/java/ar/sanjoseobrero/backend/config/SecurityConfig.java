package ar.sanjoseobrero.backend.config;

import ar.sanjoseobrero.backend.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/inscripciones").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/actividades/activas").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sedes").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/asistencia").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/asistencia/**").authenticated()
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/health").permitAll()

                // Rutas solo ADMIN 
                .requestMatchers("/api/profesores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/inscripciones").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/inscripciones/actividad/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/inscripciones/*/estado").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/inscripciones/*/sede").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/alumnos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/actividades").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/actividades/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/actividades/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}