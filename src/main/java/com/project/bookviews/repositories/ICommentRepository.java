package com.project.bookviews.repositories;

import com.project.bookviews.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
//    List<Comment> findByUserId(@Param("userId") Long userId);

    Page<Comment> findAll(Pageable pageable);
    List<Comment> findByUserIdAndEbookId(@Param("userId") Long userId,
                                         @Param("ebookId") Long ebookId);

    List<Comment> findByEbookId(@Param("ebookId") Long ebookId);
}
