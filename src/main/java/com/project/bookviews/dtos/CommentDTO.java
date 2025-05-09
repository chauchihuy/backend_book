package com.project.bookviews.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CommentDTO {

    @JsonProperty("ebook_id")
    private Long ebookId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("content")
    private String content;
//
    @JsonProperty("evaluate")
    private String evaluate;

}
