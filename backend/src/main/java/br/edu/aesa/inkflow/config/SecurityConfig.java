package br.edu.aesa.inkflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração mínima de segurança da Etapa 1: API sem estado (stateless),
 * apenas o endpoint de saúde é público. A autenticação por token (JWT) e a
 * autorização por perfil entram na Etapa 2.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(regras -> regras
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
