package com.example.bookstore.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequest {
    @NotNull
    @Min(0)
    private Long bookId;
    @NotNull
    @Min(0)
    private int quantity;
}
