package com.project.cloud.Repo;

import com.project.cloud.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserUsername(String username);
}
