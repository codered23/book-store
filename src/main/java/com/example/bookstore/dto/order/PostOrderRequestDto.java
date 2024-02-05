package com.example.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostOrderRequestDto {
    @NotNull
    private String shippingAddress;
}
