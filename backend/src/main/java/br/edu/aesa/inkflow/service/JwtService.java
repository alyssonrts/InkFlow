package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.domain.model.Usuario;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Emissão de tokens de acesso. O token carrega o e-mail (sub) e o perfil do usuário. */
@Service
public class JwtService {

    private final JwtEncoder codificador;
    private final Duration validade;

    public JwtService(JwtEncoder codificador, @Value("${inkflow.jwt.expiracao-minutos}") long expiracaoEmMinutos) {
        this.codificador = codificador;
        this.validade = Duration.ofMinutes(expiracaoEmMinutos);
    }

    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        JwtClaimsSet.Builder reivindicacoes = JwtClaimsSet.builder()
                .issuer("inkflow")
                .issuedAt(agora)
                .expiresAt(agora.plus(validade))
                .subject(usuario.getEmail())
                .claim("perfil", usuario.getPerfil().name());
        if (usuario.getId() != null) {
            reivindicacoes.claim("uid", usuario.getId());
        }
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        return codificador.encode(JwtEncoderParameters.from(cabecalho, reivindicacoes.build())).getTokenValue();
    }

    public long validadeEmSegundos() {
        return validade.toSeconds();
    }
}
