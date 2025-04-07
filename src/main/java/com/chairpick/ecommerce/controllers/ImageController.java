package com.chairpick.ecommerce.controllers;

import com.chairpick.ecommerce.services.ChairImageLocatorService;
import jdk.jfr.ContentType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller()
@RequestMapping("/images")
public class ImageController {

    private final ChairImageLocatorService chairImageLocatorService;

    public ImageController(ChairImageLocatorService chairImageLocatorService) {
        this.chairImageLocatorService = chairImageLocatorService;
    }

    @GetMapping("/chairs/{id}")
    public ResponseEntity<Resource> getImageForChair(@PathVariable Long id) throws IOException {
        Path imagePath = chairImageLocatorService.getChairImage(id);
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
