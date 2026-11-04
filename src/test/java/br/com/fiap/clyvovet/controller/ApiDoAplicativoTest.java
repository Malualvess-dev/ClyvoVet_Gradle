package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.RelogioAjustavel;
import br.com.fiap.clyvovet.RelogioDeTesteConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** API REST consumida pelo aplicativo mobile, sobre os dados do grupo (H2 criado pelo Flyway). */
@SpringBootTest
@AutoConfigureMockMvc
@Import(RelogioDeTesteConfig.class)
@Transactional
class ApiDoAplicativoTest {

    private static final String SENHA = "Clyvo@2026";
    private static final RequestPostProcessor TUTOR = httpBasic("joao.tutor@clyvo.com", SENHA);
    private static final RequestPostProcessor VETERINARIO = httpBasic("vet.roberto@clyvo.com", SENHA);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RelogioAjustavel relogio;

    @BeforeEach
    void relogioNaSegunda() {
        relogio.ajustarPara(RelogioDeTesteConfig.SEGUNDA_AS_NOVE);
    }

    @Test
    void semCredenciaisRespondeJson401SemAbrirJanelaDeLogin() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos."));
    }

    @Test
    void senhaErradaEhRecusada() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("joao.tutor@clyvo.com", "errada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginDoTutorDevolveOPerfilNoFormatoDoApp() throws Exception {
        mockMvc.perform(get("/api/me").with(TUTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("tutor"))
                .andExpect(jsonPath("$.nome").value("João Victor"))
                .andExpect(jsonPath("$.cpf").value("111.111.111-11"))
                .andExpect(jsonPath("$.whatsapp").value("(11) 99999-0001"));
    }

    @Test
    void loginDoVeterinarioDevolveCrmv() throws Exception {
        mockMvc.perform(get("/api/me").with(VETERINARIO))
                .andExpect(jsonPath("$.tipo").value("veterinario"))
                .andExpect(jsonPath("$.crmv").value("CRMV-SP-12345"));
    }

    @Test
    void cadastroDeTutorPeloAppCriaContaQueJaConsegueEntrar() throws Exception {
        mockMvc.perform(json(post("/api/cadastro/tutor"), """
                        {"nome":"Diego Prado","email":"diego@exemplo.com","senha":"Segura123",
                         "cpf":"529.982.247-25","whatsapp":"(11) 98765-4321","dataNascimento":"1999-04-10"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("tutor"))
                .andExpect(jsonPath("$.cpf").value("529.982.247-25"));

        mockMvc.perform(get("/api/me").with(httpBasic("diego@exemplo.com", "Segura123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Diego Prado"));
    }

    @Test
    void cadastroDeVeterinarioValidaOCrmvDoBanco() throws Exception {
        mockMvc.perform(json(post("/api/cadastro/veterinario"), """
                        {"nome":"Dra. Paula","email":"paula@exemplo.com","senha":"Segura123","crmv":"CRMV-SP 999",
                         "telefone":"(11) 98765-4321","especialidade":"Clínica","dataNascimento":"1990-01-01"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.crmv").exists());
    }

    @Test
    void emailJaCadastradoEhRecusado() throws Exception {
        mockMvc.perform(json(post("/api/cadastro/tutor"), """
                        {"nome":"Outro João","email":"joao.tutor@clyvo.com","senha":"Segura123",
                         "cpf":"529.982.247-25","whatsapp":"(11) 98765-4321","dataNascimento":"1999-04-10"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Já existe uma conta cadastrada com este e-mail."));
    }

    @Test
    void tutorListaSeusPetsComIdadeEPesoFormatados() throws Exception {
        mockMvc.perform(get("/api/pets").with(TUTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Thor"))
                .andExpect(jsonPath("$[0].especie").value("Cão"))
                .andExpect(jsonPath("$[0].sexo").value("Macho"))
                .andExpect(jsonPath("$[0].idade").value("5 anos"));
    }

    @Test
    void veterinarioVeTodosOsPacientes() throws Exception {
        mockMvc.perform(get("/api/pets").with(VETERINARIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Mingau")));
    }

    @Test
    void tutorCadastraEditaEExcluiPet() throws Exception {
        String resposta = mockMvc.perform(json(post("/api/pets").with(TUTOR), """
                        {"nome":"Pipoca","racaId":6,"sexo":"F","dataNascimento":"2024-02-10","pesoKg":8.5,
                         "observacoes":"Alergia a frango"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.raca").value("SRD (sem raça definida)"))
                .andExpect(jsonPath("$.peso").value("8,5"))
                .andExpect(jsonPath("$.observacoes").value("Alergia a frango"))
                .andReturn().getResponse().getContentAsString();
        String id = resposta.replaceAll(".*\"id\":\"(\\d+)\".*", "$1");

        mockMvc.perform(json(put("/api/pets/" + id).with(TUTOR), """
                        {"nome":"Pipoca","racaId":6,"sexo":"F","dataNascimento":"2024-02-10","pesoKg":9.1}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peso").value("9,1"));

        mockMvc.perform(delete("/api/pets/" + id).with(TUTOR)).andExpect(status().isNoContent());
    }

    @Test
    void petComHistoricoNaoPodeSerExcluido() throws Exception {
        mockMvc.perform(delete("/api/pets/1").with(TUTOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(
                        "Thor já possui consultas ou histórico registrados e não pode ser excluído."));
    }

    @Test
    void petInvalidoDevolveOsCamposComErro() throws Exception {
        mockMvc.perform(json(post("/api/pets").with(TUTOR), "{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.racaId").exists())
                .andExpect(jsonPath("$.campos.pesoKg").exists());
    }

    @Test
    void veterinarioNaoCadastraPet() throws Exception {
        mockMvc.perform(json(post("/api/pets").with(VETERINARIO), """
                        {"nome":"Rex","racaId":6,"sexo":"M","dataNascimento":"2024-02-10","pesoKg":8}"""))
                .andExpect(status().isForbidden());
    }

    @Test
    void horariosDisponiveisSeguemAPoliticaDaClinica() throws Exception {
        mockMvc.perform(get("/api/veterinarios/1/horarios").param("data", "2026-10-20").with(TUTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("08:00"))
                .andExpect(jsonPath("$", hasItem("17:30")));
    }

    @Test
    void fluxoCompletoPeloAppDoPedidoAoAtendimento() throws Exception {
        String id = solicitarConsultaDoThor("2026-10-20", "10:00");

        mockMvc.perform(get("/api/veterinarios/1/horarios").param("data", "2026-10-20").with(TUTOR))
                .andExpect(jsonPath("$", not(hasItem("10:00"))));

        mockMvc.perform(post("/api/consultas/" + id + "/confirmacao").with(VETERINARIO))
                .andExpect(jsonPath("$.status").value("confirmada"));

        mockMvc.perform(json(post("/api/consultas/" + id + "/remarcacao").with(TUTOR),
                        "{\"data\":\"2026-10-21\",\"horario\":\"14:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("solicitada"))
                .andExpect(jsonPath("$.data").value("21/10/2026"))
                .andExpect(jsonPath("$.horario").value("14:00"));

        mockMvc.perform(post("/api/consultas/" + id + "/confirmacao").with(VETERINARIO))
                .andExpect(jsonPath("$.status").value("confirmada"));

        relogio.ajustarPara(LocalDateTime.of(2026, 10, 21, 14, 30));
        mockMvc.perform(json(post("/api/consultas/" + id + "/atendimento").with(VETERINARIO),
                        "{\"observacaoVeterinario\":\"Saudável. Manter a alimentação.\",\"pesoKg\":31.2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("realizada"));

        mockMvc.perform(get("/api/consultas").with(TUTOR))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].status").value("realizada"))
                .andExpect(jsonPath("$[0].observacaoVeterinario").value("Saudável. Manter a alimentação."));
        mockMvc.perform(get("/api/pets").with(TUTOR))
                .andExpect(jsonPath("$[0].peso").value("31,2"));
    }

    @Test
    void veterinarioRecusaSolicitacaoETutorExcluiORegistro() throws Exception {
        String id = solicitarConsultaDoThor("2026-10-22", "09:00");

        mockMvc.perform(post("/api/consultas/" + id + "/cancelamento").with(VETERINARIO))
                .andExpect(jsonPath("$.status").value("recusada"))
                .andExpect(jsonPath("$.justificativa").value("Desmarcada pelo veterinário no aplicativo."));

        mockMvc.perform(delete("/api/consultas/" + id).with(TUTOR)).andExpect(status().isNoContent());
    }

    @Test
    void tutorCancelaSemJustificativaETextoPadraoVaiParaOBanco() throws Exception {
        String id = solicitarConsultaDoThor("2026-10-22", "11:00");

        mockMvc.perform(post("/api/consultas/" + id + "/cancelamento").with(TUTOR))
                .andExpect(jsonPath("$.status").value("cancelada"))
                .andExpect(jsonPath("$.justificativa").value("Cancelada pelo tutor no aplicativo."));
    }

    @Test
    void consultaRealizadaNaoPodeSerExcluida() throws Exception {
        mockMvc.perform(delete("/api/consultas/1").with(TUTOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(
                        "Consultas realizadas fazem parte do prontuário do pet e não podem ser excluídas."));
    }

    @Test
    void horarioForaDoExpedienteEhRecusado() throws Exception {
        mockMvc.perform(json(post("/api/consultas").with(TUTOR), """
                        {"petId":1,"veterinarioId":1,"data":"2026-10-20","horario":"19:00",
                         "motivo":"Consulta de rotina"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    private String solicitarConsultaDoThor(String data, String horario) throws Exception {
        String resposta = mockMvc.perform(json(post("/api/consultas").with(TUTOR), """
                        {"petId":1,"veterinarioId":1,"data":"%s","horario":"%s","motivo":"Consulta de rotina"}"""
                        .formatted(data, horario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("solicitada"))
                .andExpect(jsonPath("$.nomePet").value("Thor"))
                .andExpect(jsonPath("$.veterinario").value("Dr. Roberto Santos"))
                .andReturn().getResponse().getContentAsString();
        return resposta.replaceAll("^\\{\"id\":\"(\\d+)\".*", "$1");
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder requisicao, String corpo) {
        return requisicao.contentType(MediaType.APPLICATION_JSON).content(corpo);
    }
}
