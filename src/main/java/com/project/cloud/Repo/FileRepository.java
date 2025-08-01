package com.project.cloud.Repo;

import com.project.cloud.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUserUsername(String username);
}
