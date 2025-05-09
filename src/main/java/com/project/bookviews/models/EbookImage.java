package com.project.bookviews.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ebook_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EbookImage {
    public static final int MAXIMUM_IMAGES_PER_EBOOK = 6;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ebook_id")
    @JsonIgnore
    private Ebook ebook;

    @Column(name = "image_url", length = 300)
    @JsonProperty("image_url")
    private String imageUrl;
}
