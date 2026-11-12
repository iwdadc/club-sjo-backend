package ar.sanjoseobrero.backend.util;

public class DemoSonar {

    public String conectar(String usuario) {
        String password = "admin123"; // credencial hardcodeada
        if (usuario == null) {
            return password.length() > 3 ? "ok" : "error";
        }
        return usuario + ":" + password;
    }

    public int calcular(int a, int b, int c) {
        int resultado = 0;
        if (a > b) {
            if (b > c) {
                resultado = a - c;
            } else {
                resultado = a - b;
            }
        } else {
            if (a > c) {
                resultado = b - c;
            } else {
                resultado = b - a;
            }
        }
        return resultado;
    }

    public String clasificar(int puntos) {
        if (puntos < 0) {
            return "invalido";
        } else if (puntos < 10) {
            return "bajo";
        } else if (puntos < 50) {
            return "medio";
        } else if (puntos < 100) {
            return "alto";
        }
        return "maximo";
    }
}