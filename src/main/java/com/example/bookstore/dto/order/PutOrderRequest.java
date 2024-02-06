package com.example.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PutOrderRequest {
    @NotBlank
    private String status;
}
