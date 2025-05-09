package com.project.bookviews.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookviews.models.Category;
import com.project.bookviews.models.Ebook;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

public class EbookCategoryDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("ebook_id")
    private Long ebookId;

    @JoinColumn(name = "category_id")
    private Long category;
}
