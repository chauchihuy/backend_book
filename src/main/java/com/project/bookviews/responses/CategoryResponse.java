package com.project.bookviews.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookviews.models.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("category")
    private Category category;

    public static CategoryResponse fromCategory(Category category) {
        return CategoryResponse.builder()
                .category(category)
                .build();
    }
}
