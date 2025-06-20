package com.example.app_gestione_eventi.controller;

import com.example.app_gestione_eventi.entity.User;
import com.example.app_gestione_eventi.security.JwtService;
import com.example.app_gestione_eventi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // Registrazione nuovo utente
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String role) {
        try {
            User.Role userRole;
            try {
                userRole = User.Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Ruolo non valido: deve essere UTENTE o ORGANIZZATORE");
            }

            User user = userService.registerUser(email, password, userRole);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password) {
        Optional<User> userOpt = userService.authenticate(email, password);

        if (userOpt.isPresent()) {
            String token = jwtService.generateToken(userOpt.get().getEmail());
            return ResponseEntity.ok().body(new JwtResponse(token));
        } else {
            return ResponseEntity.status(401).body("Credenziali non valide");
        }
    }

    private record JwtResponse(String token) {}
}
