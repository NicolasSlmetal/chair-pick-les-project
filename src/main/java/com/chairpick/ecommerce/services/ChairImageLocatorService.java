package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.EntityNotFoundException;
import com.chairpick.ecommerce.projections.ChairAvailableProjection;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ChairImageLocatorService {

    private static final String BASE_DIR = System.getProperty("user.dir");

    private static final String IMAGE_DIR = "/images/";

    public Path getChairImage(Long chairId) {
        Path imagePath = Path.of(BASE_DIR + IMAGE_DIR);
        try (Stream<Path> imagesDir = Files.list(imagePath)) {
            return imagesDir
                    .filter(image -> image
                            .getFileName()
                            .toString()
                            .split("\\.")
                            [0]
                            .contains(chairId.toString()))
                    .findFirst().orElse(Path.of(BASE_DIR + IMAGE_DIR + "default.png"));
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new EntityNotFoundException("Image directory not found");
        }
    }

    public Map<Long, Path> getImagesForChairs(List<ChairAvailableProjection> chairs) {
        Map<Long, Path> images = new HashMap<>();
        for (ChairAvailableProjection chair : chairs) {
            Path imagePath = getChairImage(chair.getId());
            images.put(chair.getId(), imagePath);
        }
        return images;
    }
}
