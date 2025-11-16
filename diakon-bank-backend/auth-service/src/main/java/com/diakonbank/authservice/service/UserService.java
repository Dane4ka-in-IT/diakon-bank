package com.diakonbank.authservice.service;
import com.diakonbank.authservice.dto.request.RegistrationRequest;
import com.diakonbank.authservice.entity.Role;
import com.diakonbank.authservice.entity.User;
import com.diakonbank.authservice.exception.UserAlreadyExistsException;
import com.diakonbank.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    public User registerNewUser(RegistrationRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with username: " + registrationRequest.getUsername());
        }
        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        newUser.setRoles(Set.of(Role.USER));
        return userRepository.save(newUser);
    }
}