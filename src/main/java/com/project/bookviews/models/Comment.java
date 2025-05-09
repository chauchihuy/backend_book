package com.project.bookviews.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ebook_id")
    @JsonBackReference
    private Ebook ebook;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    private String content;

    private Integer evaluate;


//    @Column(name="created_at")
//    private LocalDate createdAt;
//
//    @Column(name="updated_at")
//    private LocalDate updatedAt;

}
