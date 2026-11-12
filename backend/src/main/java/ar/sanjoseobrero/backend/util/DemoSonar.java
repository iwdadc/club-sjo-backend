package ar.sanjoseobrero.backend.util;

public class DemoSonar {
    public String conectar(String usuario) {
        String password = "admin123"; // credencial hardcodeada
        if (usuario == null) {
            return password.length() > 3 ? "ok" : "error";
        }
        return usuario + ":" + password;
    }
}