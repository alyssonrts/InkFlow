package br.edu.aesa.inkflow.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.aesa.inkflow.config.ErroDeSegurancaHandler;
import br.edu.aesa.inkflow.config.JwtConfig;
import br.edu.aesa.inkflow.config.SecurityConfig;
import br.edu.aesa.inkflow.service.TatuadorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Verifica [NF-03]: 401 sem token e 403 para perfil sem permissão. */
@WebMvcTest(controllers = TatuadorController.class)
@Import({SecurityConfig.class, JwtConfig.class, ErroDeSegurancaHandler.class})
@TestPropertySource(properties = {
        "inkflow.jwt.secret=segredo-de-teste-com-mais-de-32-caracteres!",
        "inkflow.cors.origens=http://localhost:5173"
})
class SegurancaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TatuadorService tatuadorService;

    @Test
    void requisicaoSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/tatuadores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("NAO_AUTENTICADO"));
    }

    @Test
    void clientePodeListarTatuadores() throws Exception {
        mockMvc.perform(get("/api/v1/tatuadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tatuadores listados com sucesso."));
    }

    @Test
    void clienteNaoPodeCadastrarTatuador() throws Exception {
        mockMvc.perform(post("/api/v1/tatuadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("ACESSO_NEGADO"));
    }

    @Test
    void administradorComDadosInvalidosRecebe400() throws Exception {
        mockMvc.perform(post("/api/v1/tatuadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDACAO"));
    }
}
