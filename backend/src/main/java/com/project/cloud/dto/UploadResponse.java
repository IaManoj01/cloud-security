package com.project.cloud.dto;

public class UploadResponse {
    private Long fileId;
    private String message;

    public UploadResponse(Long fileId, String message) {
        this.fileId = fileId;
        this.message = message;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getMessage() {
        return message;
    }
}
