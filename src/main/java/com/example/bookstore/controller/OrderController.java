package com.example.bookstore.controller;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PostOrderRequestDto;
import com.example.bookstore.dto.order.PutOrderRequest;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by id",
            description = "Retrieve order with all information")
    OrderDto getOrderById(Authentication authentication,
                                        @PathVariable Long orderId) {
        return orderService.getOrderById(authentication, orderId);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all orderItems from order",
            description = "Retrieve a list of all orderItems by order id")
    List<OrderItemDto> getAllOrderItems(Authentication authentication,
                                        @PathVariable Long orderId, Pageable pageable) {
        return orderService.getAllOrderItems(authentication, orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get item from order",
            description = "Retrieve item by id from order")
    OrderItemDto getOrderItemBy(Authentication authentication, @PathVariable Long orderId,
                                @PathVariable Long itemId) {
        return orderService.getOrderItemBy(authentication, orderId, itemId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "update order status",
            description = "Change status in specific order")
    OrderDto updateOrderStatus(Authentication authentication,
                               @PathVariable Long id,
                               @RequestBody PutOrderRequest dto) {
        return orderService.updateStatus(authentication, id, dto);
    }
}
