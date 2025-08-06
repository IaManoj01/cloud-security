package com.project.cloud.controller;

import com.project.cloud.Repo.FileRepository;
import com.project.cloud.model.File;
import com.project.cloud.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    private final FileRepository fileRepo;

    public FileController(FileService fileService, FileRepository fileRepo) {
        this.fileService = fileService;
        this.fileRepo = fileRepo;
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> upload(@RequestParam(name = "file") MultipartFile file,
                                                 @RequestParam(name = "username") String username) {
        return fileService.encryptAndSave(file, username);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") Long id) {
        System.out.println("Received request to download file with ID: " + id);
        return fileService.decryptAndDownload(id);
    }

    @GetMapping("/files")
    public List<File> listFiles(@RequestParam String username) {
        return fileRepo.findByUserUsername(username);
    }
}
