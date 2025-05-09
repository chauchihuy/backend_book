package com.project.bookviews.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value=1, message = "Order's ID must be > 0")
    private Long orderId;

    @Min(value=1, message = "Ebook's ID must be > 0")
    @JsonProperty("ebook_id")
    private Long ebookId;

    @Min(value=0, message = "Ebook's ID must be >= 0")
    private Double price;

    @Min(value=0, message = "total_money must be >= 0")
    @JsonProperty("total_money")
    private Double totalMoney;

    private String color;
}
