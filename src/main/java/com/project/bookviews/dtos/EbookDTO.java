package com.project.bookviews.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookviews.models.KindOfBook;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EbookDTO {
    @JsonProperty("category_id")
    @NotNull(message = "Category ID cannot be null")
    private List<Long> categoryId;

    @NotEmpty(message = "Tên không để trống")
    @Size(min = 3, max = 100, message = "Tên từ 3-255 kí tự")
    private String name;

    @NotEmpty(message = "Tiêu đề không để trống")
    @Size(min = 3, max = 255, message = "Tên từ 3-255 kí tự")
    private String title;

    @NotNull(message = "Loại không để trống")
    private KindOfBook kindofbook;

    @Size(min = 3)
    private String document;

    private String thumbnail;

//    @JsonProperty("audio_url")
//    private String audioUrl;

    private Integer evaluate;

    private double price;

}
