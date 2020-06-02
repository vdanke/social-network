package org.step.social.network.service;

import org.step.social.network.model.User;
import org.step.social.network.payload.request.RegistrationRequest;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User save(RegistrationRequest request);
}
