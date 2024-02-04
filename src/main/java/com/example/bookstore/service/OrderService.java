package com.example.bookstore.service;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PostOrderRequestDto;
import com.example.bookstore.dto.order.PutOrderRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Authentication authentication, PostOrderRequestDto dto);

    List<OrderDto> getAll(Authentication authentication, Pageable pageable);

    List<OrderItemDto> getAllOrderItems(Authentication authentication, Long orderId, Pageable pageable);

    OrderDto updateStatus(Authentication authentication, Long orderId, PutOrderRequest dto);

    OrderItemDto getOrderItemBy(Authentication authentication, Long orderId, Long orderItemId);
}
