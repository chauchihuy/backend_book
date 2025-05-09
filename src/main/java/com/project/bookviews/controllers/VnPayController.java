package com.project.bookviews.controllers;

import com.project.bookviews.models.OrderRequest;
import com.project.bookviews.models.OrderStatus;
import com.project.bookviews.services.oders.OrderService;
import com.project.bookviews.services.oders.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${api.prefix}")
public class VnPayController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private OrderService orderService;
    @GetMapping("")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("VNPay Integration API is ready!", HttpStatus.OK);
    }

    @PostMapping("/submitOrder")
    public ResponseEntity<String> submitOrder(@RequestBody OrderRequest orderRequest,
                                              HttpServletRequest request) {
        // Log the received parameters
        System.out.println("Received amount: " + orderRequest.getAmount());
        System.out.println("Received orderInfo: " + orderRequest.getOrderInfo());

        try {
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + "/api/v1"; // Thiếu chỗ này nè
            String vnpayUrl = vnPayService.createOrder(orderRequest.getAmount(), orderRequest.getOrderInfo(), baseUrl);
            // Return the URL directly for frontend redirection
            return ResponseEntity.ok(vnpayUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing order");
        }
    }
    @GetMapping("/vnpay-payment")
    public ResponseEntity<String> vnpayPayment(HttpServletRequest request, HttpServletResponse response) {
        try {
            int paymentStatus = vnPayService.orderReturn(request);

            String orderInfo = request.getParameter("vnp_OrderInfo");
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPrice = request.getParameter("vnp_Amount");

            // Tạo chuỗi query với chi tiết thanh toán
            String queryString = String.format("?orderInfo=%s&paymentTime=%s&transactionId=%s&totalPrice=%s",
                    orderInfo, paymentTime, transactionId, totalPrice);

            // Cập nhật trạng thái đơn hàng nếu thanh toán thành công
            if (paymentStatus == 1) {
                Long orderId = Long.parseLong(orderInfo); // Giả sử orderInfo chứa orderId
                orderService.updateOrderStatus(orderId, OrderStatus.COMPLETE);
                String redirectUrl = "http://localhost:4200/payment-success" + queryString;
                response.sendRedirect(redirectUrl);
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
            } else {
                Long orderId = Long.parseLong(orderInfo); // Giả sử orderInfo chứa orderId
                orderService.updateOrderStatus(orderId, OrderStatus.CANCEL);
                String redirectUrl = "http://localhost:4200/payment-failure" + queryString;
                response.sendRedirect(redirectUrl);
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling VNPay payment");
        }
    }
}
