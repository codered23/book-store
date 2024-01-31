package com.example.bookstore.dto.cart;

import lombok.Data;
import java.util.Set;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<Long> cartItemIds;
    private boolean isDeleted;
}
