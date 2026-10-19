package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import br.com.fiap.clyvovet.repository.UsuarioRepository;
import br.com.fiap.clyvovet.security.UsuarioLogado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Import(RelogioDeTesteConfig.class)
@Transactional
class PaginasESegurancaTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @ParameterizedTest
    @ValueSource(strings = {"/tutor/painel", "/veterinario/agenda", "/notificacoes"})
    void paginasProtegidasRedirecionamVisitanteParaLogin(String pagina) throws Exception {
        mockMvc.perform(get(pagina))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/cadastro"})
    void paginasPublicasCarregam(String pagina) throws Exception {
        mockMvc.perform(get(pagina)).andExpect(status().isOk());
    }

    @Test
    void loginComSenhaRedirecionaParaOPainel() throws Exception {
        mockMvc.perform(formLogin("/login").user("email", "carla@clyvovet.com").password("senha", "Clyvo@2026"))
                .andExpect(redirectedUrl("/painel"));
    }

    @Test
    void loginComSenhaErradaVoltaParaLoginComErro() throws Exception {
        mockMvc.perform(formLogin("/login").user("email", "carla@clyvovet.com").password("senha", "errada"))
                .andExpect(redirectedUrl("/login?erro"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tutor/painel", "/tutor/pets", "/tutor/consultas", "/tutor/consultas/nova",
            "/tutor/consultas/nova?petId=1&veterinarioId=1&data=2026-10-20", "/notificacoes"})
    void paginasDoTutorCarregam(String pagina) throws Exception {
        mockMvc.perform(get(pagina).with(logadoComo("carla@clyvovet.com"))).andExpect(status().isOk());
    }

    @Test
    void prontuarioDoPetCarrega() throws Exception {
        mockMvc.perform(get("/tutor/pets/1").with(logadoComo("carla@clyvovet.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("tutor/pet-detalhe"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/veterinario/painel", "/veterinario/solicitacoes", "/veterinario/agenda", "/notificacoes"})
    void paginasDoVeterinarioCarregam(String pagina) throws Exception {
        mockMvc.perform(get(pagina).with(logadoComo("ana.vet@clyvovet.com"))).andExpect(status().isOk());
    }

    @Test
    void veterinarioNaoAcessaAreaDoTutor() throws Exception {
        mockMvc.perform(get("/tutor/pets").with(logadoComo("ana.vet@clyvovet.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void tutorNaoAcessaAreaDoVeterinario() throws Exception {
        mockMvc.perform(get("/veterinario/solicitacoes").with(logadoComo("carla@clyvovet.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void atendimentoDeConsultaJaConcluidaNaoAbre() throws Exception {
        mockMvc.perform(get("/veterinario/consultas/1/atendimento").with(logadoComo("ana.vet@clyvovet.com")))
                .andExpect(redirectedUrl("/painel"))
                .andExpect(flash().attributeExists(Mensagens.ERRO));
    }

    @Test
    void cadastroInvalidoExibeErrosDeValidacao() throws Exception {
        mockMvc.perform(post("/cadastro").with(csrf())
                        .param("nome", "")
                        .param("email", "invalido")
                        .param("senha", "fraca")
                        .param("confirmacaoSenha", "outra"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("conta", "nome", "email", "senha", "senhasIguais"));
    }

    @Test
    void cadastroValidoLevaParaEscolhaDePerfil() throws Exception {
        mockMvc.perform(post("/cadastro").with(csrf())
                        .param("nome", "Diego Prado")
                        .param("email", "diego@exemplo.com")
                        .param("senha", "Segura123")
                        .param("confirmacaoSenha", "Segura123"))
                .andExpect(redirectedUrl("/cadastro/perfil"));
    }

    @Test
    void formularioSemCsrfEhRejeitado() throws Exception {
        mockMvc.perform(post("/tutor/pets").with(logadoComo("carla@clyvovet.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastroDePetInvalidoExibeErros() throws Exception {
        mockMvc.perform(post("/tutor/pets").with(logadoComo("carla@clyvovet.com")).with(csrf())
                        .param("nome", "")
                        .param("pesoKg", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("pet", "nome", "especie", "dataNascimento", "pesoKg"));
    }

    private RequestPostProcessor logadoComo(String email) {
        return user(UsuarioLogado.de(usuarioRepository.findByEmail(email).orElseThrow()));
    }
}
