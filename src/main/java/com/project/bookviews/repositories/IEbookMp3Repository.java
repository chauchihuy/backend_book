package com.project.bookviews.repositories;

import com.project.bookviews.models.EbookImage;
import com.project.bookviews.models.EbookMp3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEbookMp3Repository extends JpaRepository<EbookMp3, Long> {
    List<EbookImage> findByEbookId(Long ebookID);
    Optional<EbookMp3> findById(Long id);
}
