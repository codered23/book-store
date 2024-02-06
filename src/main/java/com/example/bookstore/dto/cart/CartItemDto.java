package com.example.bookstore.dto.cart;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private Long shoppingCartId;
    private Long bookId;
    private Long quantity;
}
