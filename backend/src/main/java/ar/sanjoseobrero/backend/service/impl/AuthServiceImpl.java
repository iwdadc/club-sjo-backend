package ar.sanjoseobrero.backend.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.sanjoseobrero.backend.dto.LoginRequestDTO;
import ar.sanjoseobrero.backend.dto.LoginResponseDTO;
import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import ar.sanjoseobrero.backend.entity.enums.Rol;
import ar.sanjoseobrero.backend.repository.ProfesorRepository;
import ar.sanjoseobrero.backend.repository.UsuarioSistemaRepository;
import ar.sanjoseobrero.backend.security.JwtUtil;
import ar.sanjoseobrero.backend.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final ProfesorRepository profesorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        UsuarioSistema usuario = usuarioSistemaRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));

        if (!usuario.getActivo()) {
            throw new BadCredentialsException("El usuario está inactivo");
        }

        // Compara la contraseña en texto plano contra el hash guardado
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());

        // Obtiene el nombre — distinto según el rol
        String nombre = obtenerNombre(usuario);

        return LoginResponseDTO.builder()
            .token(token)
            .email(usuario.getEmail())
            .nombre(nombre)
            .rol(usuario.getRol())
            .build();
    }

    // Si es PROFESOR busca su nombre en la tabla Profesor
    // Si es ADMIN no tiene tabla asociada, usamos un nombre genérico
    private String obtenerNombre(UsuarioSistema usuario) {
        if (usuario.getRol() == Rol.PROFESOR) {
            return profesorRepository.findByUsuarioSistemaId(usuario.getId())
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Profesor");
        }
        return "Administrador";
    }
}
