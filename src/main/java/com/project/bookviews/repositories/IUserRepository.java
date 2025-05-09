package com.project.bookviews.repositories;

import com.project.bookviews.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);

//    @Query("SELECT o FROM User o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' OR " +

//@Query("SELECT o FROM User o WHERE (:keyword IS NULL OR :keyword = '' OR " +
//
//        "o.fullName LIKE %:keyword% " +
//            "OR o.address LIKE %:keyword% " +
//            "OR o.phoneNumber LIKE %:keyword%) " +
//            "AND LOWER(o.role.name) = 'user'")
//    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM User o WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% " +
            "OR o.address LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword%)")
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);
}

