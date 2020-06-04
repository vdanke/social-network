package org.step.social.network.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.step.social.network.configuration.AppProperties;
import org.step.social.network.configuration.security.UserDetailsImpl;
import org.step.social.network.exception.NotFoundException;
import org.step.social.network.model.Role;
import org.step.social.network.model.User;
import org.step.social.network.payload.request.RegistrationRequest;
import org.step.social.network.payload.response.UserCabinetResponse;
import org.step.social.network.payload.response.UserPublicResponse;
import org.step.social.network.repository.UserRepository;
import org.step.social.network.service.FileHelper;
import org.step.social.network.service.UserService;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final FileHelper fileHelper;

    private Path fileStorage;
    private String[] extensions;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AppProperties appProperties,
                           FileHelper fileHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
        this.fileHelper = fileHelper;
    }

    @PostConstruct
    public void init() {
        this.fileStorage = Paths.get(appProperties.getFile().getPath())
                .normalize()
                .toAbsolutePath();

        this.extensions = new String[]{"jpeg", "jpg", "png"};

        fileHelper.createFolder(this.fileStorage);
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

    @Override
    public UserCabinetResponse renderUserByPrincipal(UserDetailsImpl userDetails) {
        UserCabinetResponse cabinetResponse = new UserCabinetResponse();

        cabinetResponse.setUsername(userDetails.getUsername());
        cabinetResponse.setEnabled(userDetails.isEnabled());
        cabinetResponse.setId(userDetails.getUser().getId().toString());

        return cabinetResponse;
    }

    @Override
    public UserPublicResponse findById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", id)));

        UserPublicResponse response = new UserPublicResponse();

        response.setUsername(user.getUsername());
        response.setEnabled(user.isEnabled());
        response.setId(user.getId().toString());

        return response;
    }

    @Override
    public ResponseEntity<String> uploadImage(MultipartFile file, UserDetailsImpl userDetails) {
        String resultFileName = fileHelper.getFileNameWithUploading(file, this.fileStorage, this.extensions);

        UUID id = userDetails.getUser().getId();

        User user = findUserById(id);

        user.setFileName(resultFileName);

        userRepository.save(user);

        return new ResponseEntity<>(resultFileName.substring(36), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<?> deleteExistImage(UserDetailsImpl userDetails) {
        String fileName = userDetails.getUser().getFileName();

        boolean isFileDeleted = fileHelper.deleteExistingFile(fileName, this.fileStorage);

        if (!isFileDeleted) {
            LOGGER.error(String.format("File was not deleted %s", fileName));
        }
        return new ResponseEntity<>(isFileDeleted, HttpStatus.OK);
    }

    @Override
    public Resource downloadFile(UserDetailsImpl userDetails) {
        String fileName = userDetails.getUser().getFileName();

        if (StringUtils.isEmpty(fileName)) {
            throw new NotFoundException("File not found");
        }
        return fileHelper.getResourceFileImage(fileName, this.fileStorage);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", id.toString())));
    }
}
