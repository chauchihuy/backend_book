package com.project.bookviews.repositories;

import com.project.bookviews.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepository extends JpaRepository <Category,Long> {
    boolean existsByName(String name);
}
