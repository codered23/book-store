package com.example.bookstore.controller;

import com.example.bookstore.dto.cart.AddItemToCartRequest;
import com.example.bookstore.dto.cart.ShoppingCartDto;
import com.example.bookstore.dto.cart.UpdateItemRequest;
import com.example.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book-store api", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get shoppingCart by user",
            description = "Retrieve the shopping cart with all added items")
    public ResponseEntity<ShoppingCartDto> getShoppingCart(Authentication authentication) {
        ShoppingCartDto byUser = shoppingCartService.getByUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(byUser);
    }

    @PostMapping
    @Operation(summary = "Add item to cart",
            description = "Add a new item to the shopping cart")
    public ResponseEntity<ShoppingCartDto> createCategory(Authentication authentication,
             @RequestBody @Valid AddItemToCartRequest requestDto) {
        ShoppingCartDto shoppingCartDto = shoppingCartService.addToCart(authentication, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(shoppingCartDto);
    }

    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update quantity of item in cart",
            description = "Update the quantity of a specific item in the shopping cart")
    public ResponseEntity<ShoppingCartDto> update(@PathVariable Long id,
                                  @RequestBody @Valid UpdateItemRequest requestDto,
                                  Authentication authentication) {
        ShoppingCartDto shoppingCartDto = shoppingCartService.updateCartItem(authentication,
                id, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(shoppingCartDto);
    }

    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Remove cart-item by id",
            description = "Remove an item from the shopping cart")
    public ResponseEntity<ShoppingCartDto> getBooksByCategoryId(@PathVariable Long id,
                                                Authentication authentication) {
        ShoppingCartDto shoppingCartDto = shoppingCartService.deleteCartItem(id, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(shoppingCartDto);
    }
}
