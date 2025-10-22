package uz.brb.redis_cache.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uz.brb.redis_cache.config.CustomUserDetailsService;
import uz.brb.redis_cache.dto.request.LoginRequest;
import uz.brb.redis_cache.dto.request.RegisterRequest;
import uz.brb.redis_cache.dto.response.Response;
import uz.brb.redis_cache.entity.AuthUser;
import uz.brb.redis_cache.enums.Role;
import uz.brb.redis_cache.exceptiom.ResourceNotFoundException;
import uz.brb.redis_cache.repository.AuthUserRepository;
import uz.brb.redis_cache.service.AuthUserService;
import uz.brb.redis_cache.util.JWTUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static uz.brb.redis_cache.util.PasswordHasher.hashPassword;
import static uz.brb.redis_cache.util.PasswordValidator.validatePassword;
import static uz.brb.redis_cache.util.Util.localDateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {
    private final JWTUtil jwtUtil;
    private final AuthUserRepository authUserRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Response<?> register(RegisterRequest registerRequest) {
        Optional<AuthUser> byUsername = authUserRepository.findByUsername(registerRequest.getUsername());
        if (byUsername.isPresent()) {
            return Response.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .success(false)
                    .message("Username already exists")
                    .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                    .build();
        }
        AuthUser authUser = new AuthUser();
        authUser.setFullName(registerRequest.getFullName());
        authUser.setUsername(registerRequest.getUsername());
        authUser.setPassword(hashPassword(registerRequest.getPassword()));
        authUser.setRole(Role.USER);
        authUserRepository.save(authUser);
        return Response.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .success(true)
                .message("AuthUser successfully register")
                .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                .build();
    }

    @Override
    public Response<?> login(LoginRequest loginRequest) {
        AuthUser authUser = authUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("AuthUser not found by username: " + loginRequest.getUsername()));
        if (authUser.getUsername() == null) {
            return Response.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .success(false)
                    .message("Username not found")
                    .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                    .build();
        }
        if (!validatePassword(loginRequest.getPassword(), authUser.getPassword())) {
            return Response.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .success(false)
                    .message("Invalid password")
                    .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                    .build();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
        String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
        return Response.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .success(true)
                .message(jwtToken)
                .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                .build();
    }
}