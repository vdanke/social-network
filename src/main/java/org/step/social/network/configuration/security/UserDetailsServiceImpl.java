package org.step.social.network.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.step.social.network.exception.NotFoundException;
import org.step.social.network.model.User;
import org.step.social.network.repository.UserRepository;

import java.util.UUID;

@Service("customUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User by username %s not found", username))
                );
        return new UserDetailsImpl(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDetails loadUserById(String id) {
        UUID userId = UUID.fromString(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User by ID not found"));

        return new UserDetailsImpl(user);
    }
}
