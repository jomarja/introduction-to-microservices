package com.epam.resourceservice.controller;

import com.epam.resourceservice.Mp3;
import com.epam.resourceservice.service.ResourceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private static final MediaType MP3_MEDIA_TYPE = MediaType.parseMediaType(Mp3.MIME_TYPE);

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = Mp3.MIME_TYPE)
    public ResponseEntity<Map<String, Long>> upload(@RequestBody byte[] audioData) {
        return ResponseEntity.ok(Map.of("id", resourceService.upload(audioData)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        byte[] audioData = resourceService.download(id);
        return ResponseEntity.ok()
                .contentType(MP3_MEDIA_TYPE)
                .contentLength(audioData.length)
                .body(audioData);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> delete(@RequestParam("id") String ids) {
        return ResponseEntity.ok(Map.of("ids", resourceService.deleteByIds(ids)));
    }
}
