package org.example.backend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( a -> a
                        //Public
                        .requestMatchers("/api/habits/**").permitAll()

                        //Authenticated
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/my-habits").authenticated()
                        .requestMatchers("/api/my-habits/**").authenticated()

                        //Everything else for now
                        .anyRequest().authenticated())
                .oauth2Login(o -> o
                        .defaultSuccessUrl("http://localhost:5173/"))
                .logout(logout -> logout
                        .logoutSuccessUrl("http://localhost:5173/"));
        return http.build();


    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
