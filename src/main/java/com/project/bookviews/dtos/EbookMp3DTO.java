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
public class EbookMp3DTO {
    @JsonProperty("ebook_id")
    @Min(value = 1, message = "Ebook's ID must be > 0")
    private Long ebookId;

    @Size(min = 1, max = 200, message = "Mp3's name")
    @JsonProperty("mp3_url")
    private String mp3Url;
}
