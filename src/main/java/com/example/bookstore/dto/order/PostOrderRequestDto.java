package com.example.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostOrderRequestDto {
    @NotNull
    @NotBlank
    private String shippingAddress;
}
