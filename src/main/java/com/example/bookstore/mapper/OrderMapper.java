package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.model.Order;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "order.user.id")
    @Mapping(target = "orderItems", ignore = true)
    OrderDto toDto(Order order, OrderItemMapper orderItemMapper);

    @AfterMapping
    default void setCartItemIds(@MappingTarget OrderDto dto,
                                Order order, OrderItemMapper orderItemMapper) {
        if (order.getOrderItems() != null) {
            Set<OrderItemDto> cartItems = order.getOrderItems()
                    .stream()
                    .map(orderItemMapper::toDto)
                    .collect(Collectors.toSet());
            dto.setOrderItems(cartItems);
        }
    }
}
