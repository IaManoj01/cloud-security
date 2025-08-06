package com.project.cloud.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.project.cloud.Repo.FileRepository;
import com.project.cloud.Repo.UserRepository;
import com.project.cloud.dto.ErrorResponse;
import com.project.cloud.dto.UploadResponse;
import com.project.cloud.model.File;
import com.project.cloud.model.User;
import com.project.cloud.utils.HybridCryptoUtil;
import com.project.cloud.utils.HybridEncryptedData;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    public FileService(FileRepository fileRepository, UserRepository userRepository, CryptoService cryptoService) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }

    public ResponseEntity<Object> encryptAndSave(MultipartFile file, String username){
        try {
            byte[] inputBytes = file.getBytes();
            HybridEncryptedData encryptedData = cryptoService.hybridEncrypt(inputBytes);
            byte[] encryptedBytes = HybridCryptoUtil.packHybridData(encryptedData);

            java.io.File outFile = new java.io.File("uploads/" + file.getOriginalFilename() + ".enc");
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                fos.write(encryptedBytes);
            }

            User user = userRepository.findByUsername(username).orElseThrow();

            File meta = new File();
            meta.setFilename(file.getOriginalFilename());
            meta.setPath(outFile.getAbsolutePath());
            meta.setSize(file.getSize());
            // Store IV as Base64 string
            meta.setIv(java.util.Base64.getEncoder().encodeToString(encryptedData.getIv()));
            meta.setUploadTime(LocalDateTime.now());
            meta.setUser(user);

            fileRepository.save(meta);

            return ResponseEntity.ok(new UploadResponse(meta.getId(),"File encrypted and saved."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Encryption failed: " + e.getMessage()));
        }
    }

    public ResponseEntity<byte[]> decryptAndDownload(Long id) {
        try {
            File meta = fileRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

            java.io.File file = new java.io.File(meta.getPath());

            if (!file.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found on disk");
            }

            byte[] encrypted;
            try (FileInputStream fis = new FileInputStream(file)) {
                encrypted = fis.readAllBytes();
            }

            HybridEncryptedData data = HybridCryptoUtil.unpackHybridData(encrypted);
            // Overwrite IV from DB (for backward compatibility, only if present)
            if (meta.getIv() != null && !meta.getIv().isEmpty()) {
                data.setIv(java.util.Base64.getDecoder().decode(meta.getIv()));
            }
            byte[] decrypted = cryptoService.hybridDecrypt(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFilename() + "\"")
                    .body(decrypted);

        } catch (ResponseStatusException ex) {
            throw ex; // re-throw to return 404 or custom error
        } catch (Exception e) {
            e.printStackTrace(); // log the real issue
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to download file");
        }
    }

}
