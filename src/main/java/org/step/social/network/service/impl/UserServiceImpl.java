package org.step.social.network.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.step.social.network.configuration.AppProperties;
import org.step.social.network.model.Role;
import org.step.social.network.model.User;
import org.step.social.network.payload.request.RegistrationRequest;
import org.step.social.network.repository.UserRepository;
import org.step.social.network.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User save(RegistrationRequest request) {
        User user = new User();

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        user.setEnabled(true);
        user.setAuthorities(Collections.singleton(Role.ROLE_USER));
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
}
