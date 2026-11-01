package br.com.beneficiosonline.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BeneficioControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndListBenefit() throws Exception {
        mockMvc.perform(post("/api/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"servidorNome":"Maria Silva","servidorCpf":"12345678901","servidorOrgao":"Prefeitura","servidorMatricula":"123"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.servidorNome").value("Maria Silva"))
                .andExpect(jsonPath("$.status").value("EM_ELABORACAO"));
    }

    @Test
    void shouldRejectNonPdfDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("arquivo", "documento.txt", "text/plain",
                "conteudo".getBytes());
        mockMvc.perform(multipart("/api/beneficios/999/documentos")
                .param("categoria", "IDENTIFICACAO")
                .file(file))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldUploadAndServePdfContent() throws Exception {
        String body = "{\"servidorNome\":\"Joao Silva\",\"servidorCpf\":\"12345678901\",\"servidorOrgao\":\"Instituto\",\"servidorMatricula\":\"456\"}";
        String created = mockMvc.perform(post("/api/beneficios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Number beneficioIdValue = com.jayway.jsonpath.JsonPath.read(created, "$.id");
        long beneficioId = beneficioIdValue.longValue();
        byte[] pdf = "%PDF-1.7\nmock pdf\n%%EOF".getBytes();
        MockMultipartFile file = new MockMultipartFile("arquivo", "requerimento.pdf", "application/pdf", pdf);
        String documento = mockMvc.perform(multipart("/api/beneficios/{id}/documentos", beneficioId)
                .param("categoria", "IDENTIFICACAO").file(file))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Number documentoIdValue = com.jayway.jsonpath.JsonPath.read(documento, "$.id");
        long documentoId = documentoIdValue.longValue();
        mockMvc.perform(get("/api/beneficios/{id}/documentos/{documentoId}/conteudo", beneficioId, documentoId))
                .andExpect(status().isOk()).andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(content().bytes(pdf));
    }

    @Test
    void shouldReturnEmptyBenefitListInitially() throws Exception {
        mockMvc.perform(get("/api/beneficios"))
                .andExpect(status().isOk());
    }
}
