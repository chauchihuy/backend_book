package com.project.bookviews.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EbookImageDTO {
    @JsonProperty("ebook_id")
    @Min(value = 1, message = "Ebook's ID must be > 0")
    private Long ebookId;

    @Size(min = 1, max = 200, message = "Image's name")
    @JsonProperty("image_url")
    private String imageUrl;
}
