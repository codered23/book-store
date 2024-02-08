package com.example.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostOrderRequestDto {
    @NotBlank
    private String shippingAddress;
}
