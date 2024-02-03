package com.example.bookstore.dto.cart;

import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<Long> cartItemIds;
    private boolean isDeleted;
}
