// DataSeeder.java — Carga datos iniciales al arrancar la aplicación
// La consigna pide roles precargados — este seeder crea el usuario ADMIN
// para poder loguearse la primera vez
// Principio SRP: solo se encarga de poblar datos iniciales, nada más

package ar.sanjoseobrero.backend.config;

import ar.sanjoseobrero.backend.entity.Sede;
import ar.sanjoseobrero.backend.entity.UsuarioSistema;
import ar.sanjoseobrero.backend.entity.enums.Rol;
import ar.sanjoseobrero.backend.repository.SedeRepository;
import ar.sanjoseobrero.backend.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearAdminSiNoExiste();
        crearSedesSiNoExisten();
    }

    private void crearAdminSiNoExiste() {
        if (usuarioSistemaRepository.existsByEmail("admin@sanjoseobrero.com")) return;

        UsuarioSistema admin = UsuarioSistema.builder()
            .email("admin@sanjoseobrero.com")
            .passwordHash(passwordEncoder.encode("admin123"))
            .rol(Rol.ADMIN)
            .activo(true)
            .build();

        usuarioSistemaRepository.save(admin);
        System.out.println("✅ Usuario ADMIN creado: admin@sanjoseobrero.com / admin123");
    }

    private void crearSedesSiNoExisten() {
        if (sedeRepository.count() > 0) return;

        sedeRepository.save(Sede.builder()
            .nombre("Capilla San José Obrero")
            .direccion("Av. San Martín 7.956")
            .build());

        sedeRepository.save(Sede.builder()
            .nombre("Parroquia Sagrado Corazón de Jesús")
            .direccion("Barros Pazos 1.027")
            .build());

        sedeRepository.save(Sede.builder()
            .nombre("Cancha de Manco")
            .direccion("Calle Antofagasta, B° Las Achiras")
            .build());

        System.out.println("✅ Sedes iniciales creadas");
    }
}