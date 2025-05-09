package com.project.bookviews.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookviews.models.OrderDetail;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetailResponse {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("ebook_id")
    private Long ebookId;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("total_money")
    private Double totalMoney;

    private String color;

    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
        return OrderDetailResponse
                .builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .ebookId(orderDetail.getEbook().getId())
                .price(orderDetail.getPrice())
                .totalMoney(orderDetail.getTotalMoney())
                .color(orderDetail.getColor())
                .build();
    }

}
