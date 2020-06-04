package org.step.social.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.step.social.network.configuration.security.UserDetailsImpl;
import org.step.social.network.model.User;
import org.step.social.network.payload.request.LoginRequest;
import org.step.social.network.payload.request.RegistrationRequest;
import org.step.social.network.service.TokenProvider;
import org.step.social.network.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenProvider<UUID, Authentication> tokenProvider;

    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private int port;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          TokenProvider<UUID, Authentication> tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        UserDetailsImpl principal = (UserDetailsImpl) authenticate.getPrincipal();

        String token = tokenProvider.createToken(authenticate);

        Map<String, Object> objectMap = new HashMap<>();

        objectMap.put("username", principal.getUsername());
        objectMap.put("token", token);
        objectMap.put("redirect", "/cabinet");

        String toUriString = UriComponentsBuilder
                .newInstance()
                .host(host)
                .port(port)
                .path("/cabinet")
                .queryParam("token", token)
                .build()
                .toUriString();

        return ResponseEntity.ok()
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMap);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody RegistrationRequest registrationRequest) {
        User userAfterSaving = userService.save(registrationRequest);

        return new ResponseEntity<>(userAfterSaving, HttpStatus.CREATED);
    }
}
