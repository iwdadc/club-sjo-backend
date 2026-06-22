package ar.sanjoseobrero.backend.service;

import ar.sanjoseobrero.backend.dto.LoginRequestDTO;
import ar.sanjoseobrero.backend.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
}
