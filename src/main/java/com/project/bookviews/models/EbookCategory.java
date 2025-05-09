package com.project.bookviews.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ebook_categories")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EbookCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 1 cate nhiều ebook
    @JoinColumn(name = "ebook_id")
    private Ebook ebook;


    @ManyToOne // 1 ebook nhiều cate
    @JoinColumn(name = "category_id")
    private Category category;

}
