package org.step.social.network.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileHelper {

    void createFolder(Path pathToFolder);

    String getFileNameWithUploading(MultipartFile file, Path pathToFolder, String... extensions);

    boolean deleteExistingFile(String fileName, Path pathToFolder);

    Resource getResourceFileImage(String fileName, Path pathToFolder);
}
