package com.project.bookviews.controllers;

import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.dtos.OrderDetailDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.OrderDetail;
import com.project.bookviews.responses.OrderDetailResponse;
import com.project.bookviews.services.orderdetails.IOrderDetailService;
import com.project.bookviews.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService iOrderDetailService;
    private final LocalizationUtils localizationUtils;

    @PostMapping()
    public ResponseEntity<?> createOrderDetail( @Valid @RequestBody OrderDetailDTO orderDetailDTO)
    {

        try {
            OrderDetail newOrderDetail = iOrderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(newOrderDetail));

        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?>getOrderDetail(@Valid @PathVariable("id") Long id) throws DataNotFoundException
    {
        OrderDetail orderDetail = iOrderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?>getOrderDetails(@Valid @PathVariable("orderId") Long orderId)
    {
        List<OrderDetail> orderDetails = iOrderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses= orderDetails.stream().map(OrderDetailResponse::fromOrderDetail).toList();
        return ResponseEntity.ok(orderDetailResponses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id, @RequestBody OrderDetailDTO orderDetailDTO)
    {
        try {
//            OrderDetail orderDetail = iOrderDetailService.updateOrderDetail(id, orderDetailDTO);
            OrderDetail orderDetail = iOrderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok().body(orderDetail);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable("id") Long id)
    {
        iOrderDetailService.deleteById(id);
        return ResponseEntity.ok().body(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY));
    }



}
