package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.services.ChairImageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller()
@RequestMapping("/images")
public class ImageController {

    private final ChairImageService chairImageService;

    public ImageController(ChairImageService chairImageService) {
        this.chairImageService = chairImageService;
    }

    @GetMapping("/chairs/{id}")
    public ResponseEntity<Resource> getImageForChair(@PathVariable Long id) throws IOException {
        Path imagePath = chairImageService.getChairImage(id);
        Resource resource = new UrlResource(imagePath.toUri());
        String contentType = Files.probeContentType(imagePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }
}
