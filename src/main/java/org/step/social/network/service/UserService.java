package org.step.social.network.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.step.social.network.configuration.security.UserDetailsImpl;
import org.step.social.network.model.User;
import org.step.social.network.payload.request.RegistrationRequest;
import org.step.social.network.payload.response.UserCabinetResponse;
import org.step.social.network.payload.response.UserPublicResponse;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User save(RegistrationRequest request);

    UserCabinetResponse renderUserByPrincipal(UserDetailsImpl userDetails);

    UserPublicResponse findById(String id);
}
