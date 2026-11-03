package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import br.com.fiap.clyvovet.model.ProvedorLogin;
import br.com.fiap.clyvovet.model.Usuario;
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

    private static final String TUTOR = "joao.tutor@clyvo.com";
    private static final String VETERINARIO = "vet.roberto@clyvo.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @ParameterizedTest
    @ValueSource(strings = {"/tutor/painel", "/veterinario/agenda", "/notificacoes", "/conta/lembretes"})
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
    void usuarioDoBancoDoGrupoEntraComSenha() throws Exception {
        mockMvc.perform(formLogin("/login").user("email", TUTOR).password("senha", "Clyvo@2026"))
                .andExpect(redirectedUrl("/painel"));
    }

    @Test
    void loginComSenhaErradaVoltaParaLoginComErro() throws Exception {
        mockMvc.perform(formLogin("/login").user("email", TUTOR).password("senha", "errada"))
                .andExpect(redirectedUrl("/login?erro"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tutor/painel", "/tutor/pets", "/tutor/consultas", "/tutor/consultas/nova",
            "/tutor/consultas/nova?petId=1&veterinarioId=1&data=2026-10-20", "/notificacoes",
            "/conta/lembretes", "/conta/lembretes/1/edicao", "/conta/documentos", "/conta/endereco"})
    void paginasDoTutorCarregam(String pagina) throws Exception {
        mockMvc.perform(get(pagina).with(logadoComo(TUTOR))).andExpect(status().isOk());
    }

    @Test
    void prontuarioDoPetCarrega() throws Exception {
        mockMvc.perform(get("/tutor/pets/1").with(logadoComo(TUTOR)))
                .andExpect(status().isOk())
                .andExpect(view().name("tutor/pet-detalhe"))
                .andExpect(model().attributeExists("historicoSaude", "leituras", "idade"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/veterinario/painel", "/veterinario/solicitacoes", "/veterinario/agenda",
            "/veterinario/banco", "/notificacoes", "/conta/lembretes", "/conta/endereco"})
    void paginasDoVeterinarioCarregam(String pagina) throws Exception {
        mockMvc.perform(get(pagina).with(logadoComo(VETERINARIO))).andExpect(status().isOk());
    }

    @Test
    void veterinarioNaoAcessaAreaDoTutor() throws Exception {
        mockMvc.perform(get("/tutor/pets").with(logadoComo(VETERINARIO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tutorNaoAcessaAreaDoVeterinario() throws Exception {
        mockMvc.perform(get("/veterinario/banco").with(logadoComo(TUTOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void lembreteDeOutroUsuarioNaoEhEncontrado() throws Exception {
        mockMvc.perform(get("/conta/lembretes/2/edicao").with(logadoComo(TUTOR)))
                .andExpect(status().isNotFound());
    }

    @Test
    void atendimentoDeConsultaJaConcluidaNaoAbre() throws Exception {
        mockMvc.perform(get("/veterinario/consultas/1/atendimento").with(logadoComo(VETERINARIO)))
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
        mockMvc.perform(post("/tutor/pets").with(logadoComo(TUTOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastroDePetInvalidoExibeErros() throws Exception {
        mockMvc.perform(post("/tutor/pets").with(logadoComo(TUTOR)).with(csrf())
                        .param("nome", "")
                        .param("pesoKg", "-1")
                        .param("fotoUrl", "sem-protocolo"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("pet", "nome", "racaId", "sexo", "dataNascimento",
                        "pesoKg", "fotoUrl"));
    }

    @Test
    void cadastroDePetValidoGravaNaTabelaDoGrupo() throws Exception {
        mockMvc.perform(post("/tutor/pets").with(logadoComo(TUTOR)).with(csrf())
                        .param("nome", "Pipoca")
                        .param("racaId", "6")
                        .param("sexo", "F")
                        .param("dataNascimento", "2024-02-10")
                        .param("pesoKg", "8.5"))
                .andExpect(redirectedUrl("/tutor/pets"))
                .andExpect(flash().attributeExists(Mensagens.SUCESSO));
    }

    @Test
    void crmvForaDoFormatoDoBancoEhRecusado() throws Exception {
        mockMvc.perform(post("/cadastro/perfil/veterinario").with(user(UsuarioLogado.de(usuarioPendente())))
                        .with(csrf())
                        .param("crmv", "CRMV-SP 12345")
                        .param("especialidade", "Clínica Geral")
                        .param("telefone", "(11) 98888-0001")
                        .param("dataNascimento", "1990-01-01"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("veterinario", "crmv"));
    }

    private Usuario usuarioPendente() {
        return usuarioRepository.save(Usuario.deProvedorExterno("Diego Prado", "diego@github.com",
                ProvedorLogin.GITHUB, RelogioDeTesteConfig.SEGUNDA_AS_NOVE));
    }

    private RequestPostProcessor logadoComo(String email) {
        return user(UsuarioLogado.de(usuarioRepository.findByEmail(email).orElseThrow()));
    }
}
