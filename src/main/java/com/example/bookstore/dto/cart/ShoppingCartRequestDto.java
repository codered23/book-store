package com.example.bookstore.dto.cart;

import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartRequestDto {
    private Long userId;
    private Set<Long> cartItemIds;
    private boolean isDeleted = false;
}
