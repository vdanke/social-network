package org.step.social.network.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
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

    ResponseEntity<String> uploadImage(MultipartFile file, UserDetailsImpl userDetails);

    ResponseEntity<?> deleteExistImage(UserDetailsImpl userDetails);

    Resource downloadFile(UserDetailsImpl userDetails);
}
