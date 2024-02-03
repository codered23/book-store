package com.example.bookstore.service;

import com.example.bookstore.dto.cart.AddItemToCartRequest;
import com.example.bookstore.dto.cart.ShoppingCartDto;
import com.example.bookstore.dto.cart.UpdateItemRequest;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto addToCart(Authentication authentication, AddItemToCartRequest dto);

    ShoppingCartDto getByUser(Authentication authentication);

    ShoppingCartDto updateCartItem(Authentication authentication,
                                   Long itemId,
                                   UpdateItemRequest dto);

    ShoppingCartDto deleteCartItem(Long itemId, Authentication authentication);
}
