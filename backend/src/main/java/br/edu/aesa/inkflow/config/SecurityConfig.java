
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filtroDeSeguranca(
            HttpSecurity http,
            ErroDeSegurancaHandler erros) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())

            .sessionManagement(sessao ->
                sessao.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(regras -> regras

                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/auth/login",
                    "/api/v1/auth/registrar"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/imagens/upload"
                ).hasAnyRole("CLIENTE", "TATUADOR")

                .requestMatchers(
                    HttpMethod.GET,
                    "/uploads/**"
                ).authenticated()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/tatuadores",
                    "/api/v1/tatuadores/**"
                ).authenticated()

                .requestMatchers(
                    "/api/v1/tatuadores",
                    "/api/v1/tatuadores/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/orcamentos"
                ).hasAnyRole("CLIENTE", "TATUADOR")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/orcamentos"
                ).hasRole("CLIENTE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/orcamentos/*/emitir",
                    "/api/v1/orcamentos/*/desenho"
                ).hasRole("TATUADOR")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/orcamentos/*/decidir"
                ).hasRole("CLIENTE")

                // ETAPA 5 - Consultar disponibilidade
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/agenda/disponibilidade"
                ).hasAnyRole(
                    "CLIENTE", "TATUADOR", "ADMIN"
                )

                // ETAPA 5 - Confirmar agendamento
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/sessoes"
                ).hasAnyRole(
                    "CLIENTE", "TATUADOR", "ADMIN"
                )

                // ETAPA 5 - Consultar sessões
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/sessoes"
                ).hasAnyRole(
                    "CLIENTE", "TATUADOR", "ADMIN"
                )

                // Bloquear operações de agenda
                // ainda não autorizadas explicitamente
                .requestMatchers(
                    "/api/v1/agenda",
                    "/api/v1/agenda/**",
                    "/api/v1/sessoes",
                    "/api/v1/sessoes/**"
                ).denyAll()

                // ETAPA 5 - Permitir consulta de salas
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/salas"
                ).hasAnyRole(
                    "CLIENTE", "TATUADOR", "ADMIN"
                )

                // Administração das salas
                .requestMatchers(
                    "/api/v1/salas",
                    "/api/v1/salas/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated())

            .oauth2ResourceServer(recurso -> recurso
                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                    conversorDePerfil()))
                .authenticationEntryPoint(erros)
                .accessDeniedHandler(erros));

        return http.build();
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource configuracaoDeCors(
            @Value("${inkflow.cors.origens}") String[] origens) {

        CorsConfiguration configuracao = new CorsConfiguration();

        configuracao.setAllowedOrigins(List.of(origens));

        configuracao.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuracao.setAllowedHeaders(List.of(
            "Authorization", "Content-Type"
        ));

        UrlBasedCorsConfigurationSource fonte =
                new UrlBasedCorsConfigurationSource();

        fonte.registerCorsConfiguration(
                "/api/**", configuracao);

        fonte.registerCorsConfiguration(
                "/uploads/**", configuracao);

        return fonte;
    }

    private JwtAuthenticationConverter conversorDePerfil() {

        JwtGrantedAuthoritiesConverter autoridades =
                new JwtGrantedAuthoritiesConverter();

        autoridades.setAuthoritiesClaimName("perfil");
        autoridades.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter conversor =
                new JwtAuthenticationConverter();

        conversor.setJwtGrantedAuthoritiesConverter(autoridades);

        return conversor;
    }
}
