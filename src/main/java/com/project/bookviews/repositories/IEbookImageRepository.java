package com.project.bookviews.repositories;

import com.project.bookviews.models.EbookImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IEbookImageRepository extends JpaRepository<EbookImage, Long> {
    List<EbookImage> findByEbookId(Long ebookID);
    Optional<EbookImage> findById(Long id);
}
