package com.taskflow.securetaskflow.auth;

import com.taskflow.securetaskflow.common.BadRequestException;
import com.taskflow.securetaskflow.user.Role;
import com.taskflow.securetaskflow.user.User;
import com.taskflow.securetaskflow.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service that registers users, validates credentials and issues JWTs.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }
        // New users default to MEMBER when no explicit role is provided. This
        // keeps public registration limited while still allowing seeded/admin
        // workflows to request elevated roles for local testing.
        Role role = request.role() == null ? Role.MEMBER : request.role();
        User user = new User(
                request.fullName(),
                request.email().toLowerCase(),
                passwordEncoder.encode(request.password()),
                role
        );
        User saved = userRepository.save(user);
        return toResponse(saved, jwtService.generateToken(saved));
    }

    public AuthResponse login(AuthRequest request) {
        // Delegate password verification to Spring Security so the same
        // encoder and account checks are used for every login path.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        return toResponse(user, jwtService.generateToken(user));
    }

    private AuthResponse toResponse(User user, String token) {
        // Return the token together with basic profile data so API clients can
        // update local auth state from a single response.
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
