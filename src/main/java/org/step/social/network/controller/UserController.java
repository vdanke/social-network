package org.step.social.network.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.step.social.network.configuration.security.UserDetailsImpl;
import org.step.social.network.model.User;
import org.step.social.network.payload.response.UserCabinetResponse;
import org.step.social.network.payload.response.UserPublicResponse;
import org.step.social.network.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @PostMapping(value = "/users/{id}/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable(name = "id") String id,
            MultipartFile file
    ) {
        return userService.uploadImage(file, userDetails);
    }

    @DeleteMapping(value = "/users/{id}/file/delete")
    public ResponseEntity<?> deleteFile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable(name = "id") String id
    ) {
        return userService.deleteExistImage(userDetails);
    }

    @GetMapping(value = "/users/{id}/file/download")
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable(name = "id") String id,
            HttpServletRequest request
    ) {
        Resource downloadFile = userService.downloadFile(userDetails);

        String contentType;

        try {
            contentType = request.getServletContext().getMimeType(downloadFile.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFile.getFilename() + "\"")
                .body(downloadFile);
    }

    @GetMapping("/users/{id}")
    public UserPublicResponse getUserById(@PathVariable(name = "id") String id) {
        return userService.findById(id);
    }
}
