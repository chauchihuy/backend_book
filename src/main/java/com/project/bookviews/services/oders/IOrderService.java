package com.project.bookviews.services.oders;

import com.project.bookviews.dtos.OrderDTO;
import com.project.bookviews.dtos.OrderStatusDetailDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.Order;
import com.project.bookviews.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
ResponseEntity<?>createOder(OrderDTO orderDTO);
Order getOrder(Long id);
Order updateOder(Long id, OrderDTO orderDTO)  throws DataNotFoundException;
void deleteOder(Long id);
List<Order> finByUserId(Long userId);
    Page<OrderResponse> findByUserIdAndKeyword(Long userId, String keyword, Pageable pageable);
    Page<OrderResponse> getOrdersByUsernameAndKeyword(String username, String keyword, Pageable pageable);
Page<Order>getOdersBykeyword(String keyword, Pageable pageable);
//    Page<OrderResponse> findByUserId(Long userId, Pageable pageable);
Order updateOrderStatus(Long orderId, String status) throws DataNotFoundException;

//    public String getOrderStatus(Long orderId) throws DataNotFoundException;
//public  OrderStatusDTO (Long orderId) throws Exception;

    public List<OrderStatusDetailDTO> getAllOrdersStatus(Long userId) throws Exception ;
    Order updateOrderStatusToCancel(Long id) throws Exception;
     boolean updateActiveStatus(Long id);
}
