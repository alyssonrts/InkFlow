package br.edu.aesa.inkflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.aesa.inkflow.domain.model.Usuario;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-mais-de-32-caracteres!";
    private static final String OUTRO_SEGREDO = "outro-segredo-de-teste-com-mais-de-32-caracteres";

    private static SecretKey chave(String segredo) {
        return new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private static JwtDecoder decodificador(String segredo) {
        return NimbusJwtDecoder.withSecretKey(chave(segredo)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private static JwtService servico() {
        JwtEncoder codificador = new NimbusJwtEncoder(new ImmutableSecret<>(chave(SEGREDO)));
        return new JwtService(codificador, 60);
    }

    @Test
    void tokenGeradoContemEmailPerfilEValidade() {
        JwtService servico = servico();
        Usuario usuario = Usuario.paraAdministrador("Ana Costa", "admin@inkflow.com", "hash");

        Jwt jwt = decodificador(SEGREDO).decode(servico.gerar(usuario));

        assertEquals("admin@inkflow.com", jwt.getSubject());
        assertEquals("ADMIN", jwt.getClaimAsString("perfil"));
        assertEquals(3600L, servico.validadeEmSegundos());
        assertTrue(jwt.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void tokenAssinadoComOutraChaveEhRejeitado() {
        String token = servico().gerar(Usuario.paraAdministrador("Ana Costa", "admin@inkflow.com", "hash"));

        assertThrows(JwtException.class, () -> decodificador(OUTRO_SEGREDO).decode(token));
    }
}
