package com.project.bookviews.repositories;

import com.project.bookviews.models.Category;
import com.project.bookviews.models.EbookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEbookCategoryRepository extends JpaRepository<EbookCategory, Long> {

    List<EbookCategory> findAllByCategory(Category category);
    List<EbookCategory> findByEbookId(Long ebookId);
}
