package com.project.bookviews.repositories;

import com.project.bookviews.models.Order;
import com.project.bookviews.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullname LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword% " +
            "OR CAST(o.id AS string) LIKE %:keyword% " +
            "OR o.status LIKE %:keyword% " +
            "OR str(o.active) LIKE %:keyword% " +
            "OR function('DATE_FORMAT', o.orderDate, '%d/%m/%Y') LIKE %:keyword%)")
    Page<Order> findByUserIdAndKeyword(@Param("userId") Long userId,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);


    List<Order> findByUserId(Long userId);

//    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE  " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "o.fullname LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword% "+
            "OR str(o.id) LIKE %:keyword% " +
            "OR o.status LIKE %:keyword%" +
            "OR function('DATE_FORMAT', o.orderDate, '%d/%m/%Y') LIKE %:keyword%)")

    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.phoneNumber = :username " +
            "AND o.active = true " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullname LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword% " +
            "OR str(o.id) LIKE %:keyword% " +
            "OR o.status LIKE %:keyword% " +
            "OR function('DATE_FORMAT', o.orderDate, '%d/%m/%Y') LIKE %:keyword%)")
    Page<Order> findByUsernameAndKeyword(@Param("username") String username,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);


}
