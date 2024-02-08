package com.example.bookstore.dto.order;

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
    private String status;
    private String shippingAddress;
}
