package ar.sanjoseobrero.backend.controller;

import ar.sanjoseobrero.backend.config.SecurityConfig;
import ar.sanjoseobrero.backend.dto.AlumnoDTO;
import ar.sanjoseobrero.backend.security.JwtUtil;
import ar.sanjoseobrero.backend.service.AlumnoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de INTEGRACION WEB: levanta solo la capa web (Controller + seguridad),
 * sin base de datos ni servidor real. El Service esta mockeado.
 *
 * Se importa SecurityConfig a proposito: @WebMvcTest NO carga las clases @Configuration,
 * y sin eso las reglas de roles no se aplicarian y el test no probaria nada.
 */
@WebMvcTest(AlumnoController.class)
@Import(SecurityConfig.class)
class AlumnoControllerTest {

    private static final String BODY = "{\"nombre\":\"Juan\",\"apellido\":\"Perez\"}";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AlumnoService alumnoService;
    // JwtAuthFilter (que SecurityConfig necesita) depende de JwtUtil; lo mockeamos.
    @MockitoBean private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "prof@club.com", roles = "PROFESOR")
    @DisplayName("PROFESOR NO puede editar un alumno: 403 y el service ni se invoca")
    void profesorNoPuedeEditarAlumno() throws Exception {
        mockMvc.perform(put("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
            .andExpect(status().isForbidden());

        verifyNoInteractions(alumnoService);
    }

    @Test
    @WithMockUser(username = "admin@club.com", roles = "ADMIN")
    @DisplayName("ADMIN puede editar un alumno: 200 y devuelve el alumno actualizado")
    void adminPuedeEditarAlumno() throws Exception {
        AlumnoDTO actualizado = AlumnoDTO.builder().id(1L).nombre("Juan").apellido("Perez").build();
        when(alumnoService.actualizarDatos(eq(1L), any(AlumnoDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @DisplayName("Sin autenticacion tampoco se puede editar: error 4xx y el service ni se invoca")
    void sinLoginNoPuedeEditarAlumno() throws Exception {
        mockMvc.perform(put("/api/alumnos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
            .andExpect(status().is4xxClientError());

        verifyNoInteractions(alumnoService);
    }
}