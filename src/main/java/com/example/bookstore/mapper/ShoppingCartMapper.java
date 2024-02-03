package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.cart.ShoppingCartDto;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItemIds", ignore = true)
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setCartItemIds(@MappingTarget ShoppingCartDto dto, ShoppingCart shoppingCart) {
        if (shoppingCart.getCartItems() != null) {
            Set<Long> cartItems = shoppingCart.getCartItems()
                    .stream()
                    .map(CartItem::getId)
                    .collect(Collectors.toSet());
            dto.setCartItemIds(cartItems);
        }
    }
}
