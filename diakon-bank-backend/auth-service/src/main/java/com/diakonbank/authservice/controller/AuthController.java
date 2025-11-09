package com.diakonbank.authservice.controller;
import com.diakonbank.authservice.dto.request.ConnectBankRequest;
import com.diakonbank.authservice.dto.request.LoginRequest;
import com.diakonbank.authservice.dto.request.RegistrationRequest;
import com.diakonbank.authservice.dto.request.SyncBankRequest;
import com.diakonbank.authservice.dto.response.JwtResponse;
import com.diakonbank.authservice.entity.User;
import com.diakonbank.authservice.service.BankConnectionService;
import com.diakonbank.authservice.service.UserService;
import com.diakonbank.authservice.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BankConnectionService bankConnectionService;
    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                          UserService userService, JwtUtil jwtUtil, BankConnectionService bankConnectionService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.bankConnectionService = bankConnectionService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        userService.registerNewUser(registrationRequest);
        return ResponseEntity.ok("User registered successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final User user = userService.findByUsername(loginRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails, user);
        return ResponseEntity.ok(new JwtResponse(token));
    }
    @PostMapping("/connect-bank")
    public ResponseEntity<Void> connectBank(@RequestBody ConnectBankRequest connectBankRequest) {
        bankConnectionService.connectBank(connectBankRequest);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/sync-bank")
    public ResponseEntity<Void> syncBank(@RequestBody SyncBankRequest syncBankRequest) {
        bankConnectionService.syncBank(syncBankRequest);
        return ResponseEntity.accepted().build();
    }
}