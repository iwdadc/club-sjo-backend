package ar.sanjoseobrero.backend.controller;


import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

// Endpoint para health checks de la plataforma y smoke test del pipeline.
@RestController
public class HealthController {

    private final String commit;

    public HealthController(@Value("${RENDER_GIT_COMMIT:local}") String commit) {
        this.commit = commit;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "message", "Club San Jose Obrero API v2",
            "commit", commit
        );
    }
}
