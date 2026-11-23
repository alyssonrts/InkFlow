package br.edu.aesa.inkflow.config;

import br.edu.aesa.inkflow.dto.RespostaApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Converte as falhas de segurança no envelope padrão da API: 401 para
 * requisições sem token válido e 403 para perfil sem permissão ([NF-03]).
 */
@Component
public class ErroDeSegurancaHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapeadorJson;

    public ErroDeSegurancaHandler(ObjectMapper mapeadorJson) {
        this.mapeadorJson = mapeadorJson;
    }

    @Override
    public void commence(HttpServletRequest requisicao, HttpServletResponse resposta,
                         AuthenticationException excecao) throws IOException {
        escrever(resposta, HttpServletResponse.SC_UNAUTHORIZED,
                "Autenticação necessária ou token inválido.", "NAO_AUTENTICADO");
    }

    @Override
    public void handle(HttpServletRequest requisicao, HttpServletResponse resposta,
                       AccessDeniedException excecao) throws IOException {
        escrever(resposta, HttpServletResponse.SC_FORBIDDEN,
                "Você não tem permissão para acessar este recurso.", "ACESSO_NEGADO");
    }

    private void escrever(HttpServletResponse resposta, int status, String mensagem, String codigo)
            throws IOException {
        resposta.setStatus(status);
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setCharacterEncoding("UTF-8");
        mapeadorJson.writeValue(resposta.getWriter(), RespostaApi.erro(mensagem, codigo));
    }
}
