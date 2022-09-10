package com.siewe.pos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class ImageController {
    private final Path imageStorageDir;

    @Autowired
    public ImageController(@Value("${upload.path}") Path imageStorageDir) {
        this.imageStorageDir = imageStorageDir;
    }

    @PostConstruct
    public void ensureDirectoryExists() throws IOException {
        if (!Files.exists(this.imageStorageDir)) {
            Files.createDirectories(this.imageStorageDir);
        }
    }

    /*
   This enables you to perform POST requests against the "/image/YourID" path
   It returns the name this image can be referenced on later
    */
    @PostMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public String uploadImage(@RequestBody MultipartFile imageFile, @PathVariable("id") String id) throws IOException {
        final String fileExtension = Optional.ofNullable(imageFile.getOriginalFilename())
                .flatMap(ImageController::getFileExtension)
                .orElse("");

        final String targetFileName = id + "." + fileExtension;
        final Path targetPath = this.imageStorageDir.resolve(targetFileName);

        try (InputStream in = imageFile.getInputStream()) {
            try (OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE)) {
                in.transferTo(out);
            }
        }

        return targetFileName;
    }

    /*
   This enables you to download previously uploaded images
    */
    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("fileName") String fileName) {
        final Path targetPath = this.imageStorageDir.resolve(fileName);
        if (!Files.exists(targetPath)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PathResource(targetPath));
    }

    private static Optional<String> getFileExtension(String fileName) {
        final int indexOfLastDot = fileName.lastIndexOf('.');

        if (indexOfLastDot == -1) {
            return Optional.empty();
        } else {
            return Optional.of(fileName.substring(indexOfLastDot + 1));
        }
    }

}
