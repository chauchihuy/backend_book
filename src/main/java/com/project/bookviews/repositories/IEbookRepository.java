package com.project.bookviews.repositories;

import com.project.bookviews.models.Category;
import com.project.bookviews.models.Ebook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IEbookRepository extends JpaRepository<Ebook, Long> {

    boolean existsByName(String name);
//    @Query("SELECT e FROM Ebook e ORDER BY CASE WHEN e.price > 0 THEN 0 ELSE 1 END, e.price DESC")
    Page<Ebook> findAll(Pageable pageable);//phân trang


    @Query("SELECT e FROM Ebook e JOIN EbookCategory ec ON e.id = ec.ebook.id JOIN Category c ON ec.category.id = c.id WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR c.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR e.name LIKE %:keyword% OR e.title LIKE %:keyword%)" )
//            + "ORDER BY CASE WHEN e.price = 0 THEN 0 ELSE 1 END, e.price ASC")
    Page<Ebook> searchEbooks(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM Ebook p LEFT JOIN FETCH p.ebookImages WHERE p.id = :ebookId")

    Optional<Ebook> getDetailEbook(@Param("ebookId") Long ebookId);

    @Query("SELECT p FROM Ebook p WHERE p.id IN :ebookIds")
    List<Ebook> findEbooksByIds(@Param("ebookIds") List<Long> ebookIds);


    @Query("SELECT e FROM Ebook e WHERE e.active = false ")
//            +  "ORDER BY CASE WHEN e.price = 0 THEN 0 ELSE 1 END, e.price ASC")
    Page<Ebook> findAllInactiveEbooksUser(Pageable pageable);

    @Query("SELECT e FROM Ebook e JOIN EbookCategory ec ON e.id = ec.ebook.id JOIN Category c ON ec.category.id = c.id WHERE " +
            "e.active = false AND " +
            "(:categoryId IS NULL OR :categoryId = 0 OR c.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR e.name LIKE %:keyword% OR e.title LIKE %:keyword%)")
//            +   "ORDER BY CASE WHEN e.price = 0 THEN 0 ELSE 1 END, e.price ASC")
    Page<Ebook> searchInactiveEbooks(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
