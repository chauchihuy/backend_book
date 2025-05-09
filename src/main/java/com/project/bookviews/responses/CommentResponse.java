package com.project.bookviews.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookviews.models.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    @JsonProperty("content")
    private String content;

    //thông tin người dùng
    @JsonProperty("user_id")
    private UserResponse userResponse;

    //thông tin sách
    @JsonProperty("ebook_id")
    private  EbookResponse ebookResponse;

    @JsonProperty("create_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userResponse(UserResponse.fromUser(comment.getUser()))
                .ebookResponse(EbookResponse.fromEbook(comment.getEbook()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
