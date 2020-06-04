package org.step.social.network.service.impl;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.step.social.network.exception.FileStorageException;
import org.step.social.network.exception.NotFoundException;
import org.step.social.network.service.FileHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileHelperImpl implements FileHelper {

    @Override
    public void createFolder(Path pathToFolder) {
        try {
            Files.createDirectories(pathToFolder);
        } catch (IOException e) {
            throw new FileStorageException(String.format("Cannot create directory %s", pathToFolder.toString()));
        }
    }

    @Override
    public String getFileNameWithUploading(MultipartFile file, Path pathToFolder, String... extensions) {
        if (file == null) {
            throw new FileStorageException("File is null");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String resultFileName = UUID.randomUUID().toString() + fileName;

        if (fileName.contains("..")) {
            throw new FileStorageException(String.format("File name is incorrect %s", fileName));
        }
        if (extensions.length > 0) {
            Arrays.stream(extensions)
                    .filter(fileName::endsWith)
                    .findAny()
                    .orElseThrow(
                            () -> new FileStorageException(String.format("Extension of file %s is incorrect", fileName))
                    );
        }
        Path targetFileLocation = pathToFolder.resolve(resultFileName);

        try {
            Files.copy(file.getInputStream(), targetFileLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException(String.format("Cannot save the file %s", fileName));
        }
        return resultFileName;
    }

    @Override
    public boolean deleteExistingFile(String fileName, Path pathToFolder) {
        Path pathToFile = pathToFolder.resolve(fileName).normalize();

        String cleanFileName = fileName.substring(36);

        try {
            return Files.deleteIfExists(pathToFile);
        } catch (IOException e) {
            throw new FileStorageException(String.format("Cannot delete the file %s", cleanFileName));
        }
    }

    @Override
    public Resource getResourceFileImage(String fileName, Path pathToFolder) {
        try {
            Path pathToFile = pathToFolder.resolve(fileName).normalize();

            Resource resource = new UrlResource(pathToFile.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                String cleanFileName = fileName.substring(36);

                throw new NotFoundException(String.format("Cannot find the file %s", cleanFileName));
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException(String.format("Cannot upload the file %s", e.getLocalizedMessage()));
        }
    }
}
