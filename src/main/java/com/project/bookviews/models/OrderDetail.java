package com.project.bookviews.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ebook_id")
    private Ebook ebook;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "total_money", nullable = false)
    private Double totalMoney;

    @Column(name = "color")
    private String color;


    //xóa ebook có trong đơn mua
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ebook_id", nullable = false)
//    private Ebook ebook;
}

