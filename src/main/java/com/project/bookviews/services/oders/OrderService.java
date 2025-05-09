package com.project.bookviews.services.oders;

import com.project.bookviews.dtos.CartItemDTO;
import com.project.bookviews.dtos.OrderDTO;

import com.project.bookviews.dtos.OrderStatusDetailDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.*;
import com.project.bookviews.repositories.IOrderDetailRepository;
import com.project.bookviews.repositories.IOrderRepository;
import com.project.bookviews.repositories.IEbookRepository;
import com.project.bookviews.repositories.IUserRepository;
import com.project.bookviews.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final IUserRepository iUserRepository;
    private final IOrderRepository iOrderRepository;
    private final IEbookRepository iEbookRepository;
    private final IOrderDetailRepository iOrderDetailRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ResponseEntity<?> createOder(OrderDTO orderDTO) {


        try {

            User user = iUserRepository.findById(orderDTO.getUserId()).orElse(null);
            if (Objects.isNull(user))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng có id: " + orderDTO.getUserId());


            modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
            Order order = new Order();
            modelMapper.map(orderDTO, order);
            order.setUser(user);
            order.setOrderDate(LocalDate.now());
            order.setStatus(OrderStatus.PROCESSING);
            order.setActive(true);
            order.setTotalMoney(orderDTO.getTotalMoney());

            iOrderRepository.save(order);

            // Tạo danh sách OrderDetail từ cartItems
            List<OrderDetail> orderDetails = new ArrayList<>();
            if (orderDTO.getCartItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("CartItems không được trống");
            }

            for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);

                Long ebookId = cartItemDTO.getEbookId();
                Ebook ebook = iEbookRepository.findById(ebookId).orElse(null);
                if (Objects.isNull(ebook))
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ebook not found with id: " + ebookId);

                orderDetail.setEbook(ebook);
                orderDetail.setPrice(ebook.getPrice());

                // Calculate and set the total money for the OrderDetail
                double totalMoney = ebook.getPrice();
                orderDetail.setTotalMoney(totalMoney);

                orderDetails.add(orderDetail);
            }


            iOrderDetailRepository.saveAll(orderDetails);

            return ResponseEntity.ok(OrderResponse.fromOrder(order));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    public Order getOrder(Long id) {
        Order selectedOrder = iOrderRepository.findById(id).orElse(null);
        return selectedOrder;
    }
    @Override
    @Transactional
    public Order updateOder(Long id, OrderDTO orderDTO)  throws DataNotFoundException {
//        orderDTO.setShippingDate(LocalDate.now());
        Order order = iOrderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find order with id: " + id));
        User existingUser = iUserRepository.findById(
                orderDTO.getUserId()).orElseThrow(() ->
                new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
//        orderDTO.setShippingDate(order.getShippingDate());
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        return iOrderRepository.save(order);
    }

//    @Override
//    @Transactional
//    public void deleteOder(Long id) {
//        Order order = iOrderRepository.findById(id).orElse(null);
//        //no hard-delete, => please soft-delete
//        if (order != null) {
//            order.setActive(false);
//            iOrderRepository.save(order);
//        }
//    }

    @Override
    @Transactional
    public void deleteOder(Long id) {
        iOrderRepository.deleteById(id);
    }

    @Override
    public List<Order> finByUserId(Long userId) {
        return iOrderRepository.findByUserId(userId);
    }


    @Override
    public Page<OrderResponse> findByUserIdAndKeyword(Long userId, String keyword, Pageable pageable) {
        return iOrderRepository.findByUserIdAndKeyword(userId, keyword, pageable)
                .map(OrderResponse::fromOrder);
    }




    @Override
    public Page<Order> getOdersBykeyword(String keyword, Pageable pageable) {
        return iOrderRepository.findByKeyword(keyword,pageable);
    }


    @Override
    public Page<OrderResponse> getOrdersByUsernameAndKeyword(String username, String keyword, Pageable pageable) {
        return iOrderRepository.findByUsernameAndKeyword(username, keyword, pageable)
                .map(OrderResponse::fromOrder);
    }


//    @Override
//    public Page<OrderResponse> findByUserId(Long userId, Pageable pageable) {
//        Page<Order> orders = iOrderRepository.findByUserId(userId, pageable);
//        return orders.map(OrderResponse::fromOrder);
//    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) throws DataNotFoundException {
        Order order = iOrderRepository.findById(orderId).orElseThrow(() ->
                new DataNotFoundException("Không tìm thấy đơn hàng với id: " + orderId));
        order.setStatus(status);
        return iOrderRepository.save(order);
    }

//    @Override
//    @Transactional
//    public String getOrderStatus(Long orderId) throws DataNotFoundException{
//        Order order = iOrderRepository.findById(orderId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy dơn hàng" + orderId));
//
//        return order.getStatus();
//    }

    // tranjg thái đn hàng, active - status
    @Override
    public List<OrderStatusDetailDTO> getAllOrdersStatus(Long userId) {
        List<Order> orders = iOrderRepository.findByUserId(userId);

        // Trả về danh sách rỗng nếu không tìm thấy đơn hàng
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
                .flatMap(order -> order.getOrderDetails().stream()
                        .map(orderDetail -> new OrderStatusDetailDTO(order.getId(), order.getStatus(), orderDetail.getEbook().getId(), order.isActive()))
                )
                .collect(Collectors.toList());
    }



    @Override
    public Order updateOrderStatusToCancel(Long id) throws Exception {
        try {
            Order order = iOrderRepository.findById(id)
                    .orElseThrow(() -> new Exception("Order not found"));

            order.setStatus(OrderStatus.CANCEL);
            return iOrderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error updating order status to CANCEL for order ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public boolean updateActiveStatus(Long id) {
        return iOrderRepository.findById(id).map(order -> {
            order.setActive(!order.isActive()); // Toggle the active status
            iOrderRepository.save(order);
            return true;
        }).orElse(false); // Return false if the order is not found
    }


}
