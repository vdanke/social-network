package org.step.social.network.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.step.social.network.configuration.security.UserDetailsImpl;
import org.step.social.network.model.User;
import org.step.social.network.payload.response.UserCabinetResponse;
import org.step.social.network.payload.response.UserPublicResponse;
import org.step.social.network.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/account")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            value = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping
    public UserCabinetResponse getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.renderUserByPrincipal(userDetails);
    }

    @GetMapping("/users/{id}")
    public UserPublicResponse getUserById(@PathVariable(name = "id") String id) {
        return userService.findById(id);
    }
}
