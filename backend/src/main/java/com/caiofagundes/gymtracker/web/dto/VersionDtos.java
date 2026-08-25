package com.caiofagundes.gymtracker.web.dto;

public class VersionDtos {

    public record VersionResponse(ComponentVersion backend) {
    }

    public record ComponentVersion(String sha, String shortSha, String builtAt) {
    }
}
