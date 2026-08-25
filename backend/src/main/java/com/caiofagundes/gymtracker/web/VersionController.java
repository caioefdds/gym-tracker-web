package com.caiofagundes.gymtracker.web;

import com.caiofagundes.gymtracker.web.dto.VersionDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    private final String sha;
    private final String builtAt;

    public VersionController(
            @Value("${app.version.sha:unknown}") String sha,
            @Value("${app.version.built-at:unknown}") String builtAt) {
        this.sha = sha;
        this.builtAt = builtAt;
    }

    @GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VersionDtos.VersionResponse> version() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new VersionDtos.VersionResponse(component(this.sha, this.builtAt)));
    }

    private static VersionDtos.ComponentVersion component(String sha, String builtAt) {
        String shortSha = sha.length() >= 7 ? sha.substring(0, 7) : sha;
        return new VersionDtos.ComponentVersion(sha, shortSha, builtAt);
    }
}
