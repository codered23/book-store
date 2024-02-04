package com.example.bookstore.controller;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PostOrderRequestDto;
import com.example.bookstore.dto.order.PutOrderRequest;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book-store api", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place new order",
            description = "Create a new order based on the items in the shopping cart")
    OrderDto placeOrder(@RequestBody PostOrderRequestDto dto, Authentication authentication) {
        return orderService.placeOrder(authentication, dto);
    }

    @GetMapping
    @Operation(summary = "Get all orders",
            description = "Retrieve a list of all orders")
    List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable) {
        return orderService.getAll(authentication, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get all orderItems from order",
            description = "Retrieve a list of all orderItems by order id")
    List<OrderItemDto> getAllOrderItems(Authentication authentication, @PathVariable Long id, Pageable pageable) {
        return orderService.getAllOrderItems(authentication, id, pageable);
    }

    @GetMapping("/{orderId}/{itemId}")
    @Operation(summary = "Get item from order",
            description = "Retrieve item by id from order")
    OrderItemDto getOrderItemBy(Authentication authentication, @PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getOrderItemBy(authentication, orderId, itemId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update order status",
            description = "Change status in specific order")
    OrderDto updateOrderStatus(Authentication authentication, @PathVariable Long id, @RequestBody PutOrderRequest dto) {
        return orderService.updateStatus(authentication, id, dto);
    }
}
