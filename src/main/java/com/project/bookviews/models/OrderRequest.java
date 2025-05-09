package com.project.bookviews.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderRequest {
    private int amount;
    private String orderInfo;
}
