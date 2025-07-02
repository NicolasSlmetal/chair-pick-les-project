package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.model.Chair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class ChairImageService {

    private static final String BASE_DIR = System.getProperty("user.dir");

    @Value("${image.dir}")
    private String imageDir;

    public Path getChairImage(Long chairId) {
        Path imagePath = Path.of(BASE_DIR + imageDir);
        try (Stream<Path> imagesDir = Files.list(imagePath)) {
            return imagesDir
                    .filter(image -> image.getFileName()
                            .toString()
                            .startsWith("chair"))
                    .filter(image -> parseChairIdFromFileName(image)
                            .equals(chairId.toString()))
                    .findFirst().orElse(Path.of(BASE_DIR + imageDir + "/default.png"));
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new EntityNotFoundException("Image directory not found");
        }
    }

    private static String parseChairIdFromFileName(Path image) {
        return image
                .getFileName()
                .toString()
                .split("\\.")
                [0]
                .replace("chair", "");
    }

    public void saveChairImage(Chair chair, InputStream inputStream, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            fileName = "default.png";
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        Path imagePath = Path.of(BASE_DIR + imageDir + "/chair" +chair.getId() + "." + fileExtension);

        try {
            Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
