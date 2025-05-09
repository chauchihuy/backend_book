package com.project.bookviews.repositories;

import com.project.bookviews.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderDetailRepository extends JpaRepository<OrderDetail,Long> {
List<OrderDetail> findByOrderId(Long orderId);
    @Query("SELECT COUNT(od) > 0 FROM OrderDetail od WHERE od.ebook.id = :ebookId")
    boolean existsByEbookId(Long ebookId);
}
