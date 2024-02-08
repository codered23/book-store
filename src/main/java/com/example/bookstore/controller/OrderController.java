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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<OrderDto> placeOrder(@RequestBody PostOrderRequestDto dto,
                                        Authentication authentication) {
        OrderDto orderDto = orderService.placeOrder(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @GetMapping
    @Operation(summary = "Get all orders",
            description = "Retrieve a list of all orders")
    ResponseEntity<List<OrderDto>> getAllOrders(Authentication authentication, Pageable pageable) {
        List<OrderDto> orderDtoList = orderService.getAll(authentication, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orderDtoList);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by id",
            description = "Retrieve order with all information")
    ResponseEntity<OrderDto> getOrderById(Authentication authentication,
                                        @PathVariable Long orderId) {
        OrderDto orderById = orderService.getOrderById(authentication, orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderById);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all orderItems from order",
            description = "Retrieve a list of all orderItems by order id")
    ResponseEntity<List<OrderItemDto>> getAllOrderItems(Authentication authentication,
                                        @PathVariable Long orderId, Pageable pageable) {
        List<OrderItemDto> allOrderItems = orderService.getAllOrderItems(authentication,
                orderId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(allOrderItems);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get item from order",
            description = "Retrieve item by id from order")
    ResponseEntity<OrderItemDto> getOrderItemBy(Authentication authentication,
                                                @PathVariable Long orderId,
                                                @PathVariable Long itemId) {
        OrderItemDto orderItemBy = orderService.getOrderItemBy(authentication, orderId, itemId);
        return ResponseEntity.status(HttpStatus.OK).body(orderItemBy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "update order status",
            description = "Change status in specific order")
    ResponseEntity<OrderDto> updateOrderStatus(Authentication authentication,
                               @PathVariable Long id,
                               @RequestBody PutOrderRequest dto) {
        OrderDto orderDto = orderService.updateStatus(authentication, id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }
}
