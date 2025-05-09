    package com.project.bookviews.controllers;

    import com.project.bookviews.components.LocalizationUtils;
    import com.project.bookviews.dtos.OrderDTO;
    import com.project.bookviews.dtos.OrderStatusDetailDTO;
    import com.project.bookviews.models.Order;
    import com.project.bookviews.responses.OrderListResponse;
    import com.project.bookviews.responses.OrderResponse;
    import com.project.bookviews.services.oders.IOrderService;
    import com.project.bookviews.utils.MessageKeys;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.validation.BindingResult;
    import org.springframework.validation.FieldError;
    import org.springframework.web.bind.annotation.*;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("${api.prefix}/orders")
    @RequiredArgsConstructor
    public class OrderController {
        private final IOrderService iOrderService;
        private final LocalizationUtils localizationUtils;

        @PostMapping("")
        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
        public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult result)
        {
            if (result.hasErrors())
            {
                List<String> erroMessager = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(erroMessager);
            }
            return iOrderService.createOder(orderDTO);
        }


        // cho user xem lại các order ( controller )
        @GetMapping("/user/{user_id}")
        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
        public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Long userId,
                                           @RequestParam(defaultValue = "", required = false) String keyword,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int limit) {
            try {

                PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());

                Page<OrderResponse> ordersPage = iOrderService.findByUserIdAndKeyword(userId, keyword, pageRequest);
                return ResponseEntity.ok(ordersPage);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }


        //xem đơn  đặt hàng admin
        @GetMapping("/{id}")
    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<?>getOrder (@Valid @PathVariable("id") Long orderId)
        {
            try {
                Order existingOrder = iOrderService.getOrder(orderId);
                OrderResponse orderResponse=OrderResponse.fromOrder(existingOrder);
                return ResponseEntity.ok(orderResponse);

            }catch (Exception e)
            {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        //xem đơn  đặt hàng admin
        @GetMapping("order-user/{id}")
    //    @PreAuthorize("hasRole('ROLE_USER')")
        public ResponseEntity<?>getOrderUser (@Valid @PathVariable("id") Long orderId)
        {
            try {
                Order existingOrder = iOrderService.getOrder(orderId);
                OrderResponse orderResponse=OrderResponse.fromOrder(existingOrder);
                return ResponseEntity.ok(orderResponse);

            }catch (Exception e)
            {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }



        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        //PUT http://localhost:8088/api/v1/orders/2
        //công việc của admin
        public ResponseEntity<?> updateOrder(
                @Valid @PathVariable long id,
                @Valid @RequestBody OrderDTO orderDTO) {

            try {
                Order order = iOrderService.updateOder(id, orderDTO);
                return ResponseEntity.ok(order);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        // xóa order
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<?> deleteOrder(@Valid @PathVariable Long id) {
            //xóa mềm => cập nhật trường active = false
            iOrderService.deleteOder(id);
            String result = localizationUtils.getLocalizedMessage(
                    MessageKeys.DELETE_ORDER_SUCCESSFULLY, id);
            return ResponseEntity.ok().body(result);
        }

        //khóa order
        @PutMapping("/{id}/status")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<Map<String, String>> updateActiveStatus(@PathVariable Long id) {
            boolean updated = iOrderService.updateActiveStatus(id);
            Map<String, String> response = new HashMap<>();
            if (updated) {
                response.put("message", "Status updated successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Ebook not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }

        @GetMapping("/get-orders-by-keyword")
        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
        public ResponseEntity<OrderListResponse> getOrdersByKeyword(
                @RequestParam(defaultValue = "", required = false) String keyword,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int limit
        ) {
            // Tạo Pageable từ thông tin trang và giới hạn
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").descending()
            );
            Page<OrderResponse> orderPage = iOrderService
                    .getOdersBykeyword(keyword, pageRequest)
                    .map(OrderResponse::fromOrder);
            // Lấy tổng số trang
            int totalPages = orderPage.getTotalPages();
            List<OrderResponse> orderResponses = orderPage.getContent();
            return ResponseEntity.ok(OrderListResponse
                    .builder()
                    .orders(orderResponses)
                    .totalPages(totalPages)
                    .build());
        }


        @GetMapping("/user/get-orders-by-keyword")
        @PreAuthorize("hasRole('ROLE_USER')")
        public ResponseEntity<OrderListResponse> getOrdersByKeywordForUser(
                @RequestParam(defaultValue = "", required = false) String keyword,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int limit,
                @AuthenticationPrincipal UserDetails userDetails) {
            String username = userDetails.getUsername();
            PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
            Page<OrderResponse> orderPage = iOrderService.getOrdersByUsernameAndKeyword(username, keyword, pageRequest);
            int totalPages = orderPage.getTotalPages();
            List<OrderResponse> orderResponses = orderPage.getContent();
            return ResponseEntity.ok(OrderListResponse.builder().orders(orderResponses).totalPages(totalPages).build());
        }

//        @GetMapping("/{id}/status")
//        public ResponseEntity<?> getOrderStatus(@Valid @PathVariable("id") Long orderId) {
//            try {
//                String status = iOrderService.getOrderStatus(orderId);
//                return ResponseEntity.ok(status);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }

//        @GetMapping("/status/{userId}")
//        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//        public ResponseEntity<?> getAllOrdersStatus(@PathVariable("userId") Long userId) {
//            try {
//                List<OrderStatusDetailDTO> ordersStatus = iOrderService.getAllOrdersStatus(userId);
//                return ResponseEntity.ok(ordersStatus);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
        @GetMapping("/status/{userId}")
        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
        public ResponseEntity<?> getAllOrdersStatus(@PathVariable("userId") Long userId) {
            try {
                List<OrderStatusDetailDTO> ordersStatus = iOrderService.getAllOrdersStatus(userId);
                return ResponseEntity.ok(ordersStatus);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }


        @PutMapping("/cancel/{id}")
        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
        public ResponseEntity<?> updateStatusCancelOrder(
                @Valid @PathVariable long id) {

            try {
                Order updatedOrder = iOrderService.updateOrderStatusToCancel(id);
                return ResponseEntity.ok(updatedOrder);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

