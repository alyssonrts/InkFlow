package br.edu.aesa.inkflow.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Segurança da API (Etapa 2): sem estado, autenticação por token JWT e
 * autorização por perfil ([RF-02], [NF-02], [NF-03]).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filtroDeSeguranca(HttpSecurity http, ErroDeSegurancaHandler erros) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(regras -> regras
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/registrar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/tatuadores", "/api/v1/tatuadores/**").authenticated()
                .requestMatchers("/api/v1/tatuadores", "/api/v1/tatuadores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/orcamentos").hasAnyRole("CLIENTE", "TATUADOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/orcamentos").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.POST, "/api/v1/orcamentos/*/emitir").hasRole("TATUADOR")
                .requestMatchers("/api/v1/salas", "/api/v1/salas/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(recurso -> recurso
                .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorDePerfil()))
                .authenticationEntryPoint(erros)
                .accessDeniedHandler(erros));
        return http.build();
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource configuracaoDeCors(@Value("${inkflow.cors.origens}") String[] origens) {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(List.of(origens));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/api/**", configuracao);
        return fonte;
    }

    /** O claim "perfil" do token vira a autoridade ROLE_CLIENTE, ROLE_TATUADOR ou ROLE_ADMIN. */
    private JwtAuthenticationConverter conversorDePerfil() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("perfil");
        autoridades.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(autoridades);
        return conversor;
    }
}
