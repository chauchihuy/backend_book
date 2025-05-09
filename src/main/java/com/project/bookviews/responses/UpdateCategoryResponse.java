package com.project.bookviews.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UpdateCategoryResponse {
    @JsonProperty("message")
    private String message;
}
