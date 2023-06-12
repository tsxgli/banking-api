package nl.inholland.bankingapi.controller;

import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        authenticationService.register(registerRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) throws AuthenticationException {
       return authenticationService.login(loginRequestDTO.email(), loginRequestDTO.password());
    }

    @GetMapping("/hello")
    public String Hello() {
        return "Connection Established.";
    }

}
