package com.example.bookstore.dto.order;

import com.example.bookstore.model.Order;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private String orderDate;
    private BigDecimal total;
    private Order.Status status;
    private String shippingAddress;
}
