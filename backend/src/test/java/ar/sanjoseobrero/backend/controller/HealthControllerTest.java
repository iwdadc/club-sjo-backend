package ar.sanjoseobrero.backend.controller;

import ar.sanjoseobrero.backend.config.SecurityConfig;
import ar.sanjoseobrero.backend.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
  Prueba de la capa web para /health: el endpoint que usan Render y el smoke test del pipeline.
  Se importa SecurityConfig para comprobar tambien que la ruta es publica (sin login).
*/
@WebMvcTest(HealthController.class)
@Import(SecurityConfig.class)
class HealthControllerTest {

    @Autowired private MockMvc mockMvc;

    // JwtAuthFilter (que SecurityConfig necesita) depende de JwtUtil; lo mockeamos.
    @MockitoBean private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /health es publico: responde 200 con status UP sin autenticacion")
    void healthEsPublicoYResponde200() throws Exception {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}