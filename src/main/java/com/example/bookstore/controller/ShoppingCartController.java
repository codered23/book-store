package com.example.bookstore.controller;

import com.example.bookstore.dto.cart.AddItemToCartRequest;
import com.example.bookstore.dto.cart.ShoppingCartDto;
import com.example.bookstore.dto.cart.UpdateItemRequest;
import com.example.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book-store api", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    @GetMapping
    @Operation(summary = "Get shoppingCart by user",
            description = "Retrieve the shopping cart with all added items")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getByUser(authentication);
    }

    @PostMapping
    @Operation(summary = "Add item to cart",
            description = "Add a new item to the shopping cart")
    public ShoppingCartDto createCategory(Authentication authentication, @RequestBody @Valid AddItemToCartRequest requestDto) {
        return shoppingCartService.addToCart(authentication, requestDto);
    }

    @PutMapping("/updateItem/{id}")
    @Operation(summary = "Update quantity of item in cart",
            description = "Update the quantity of a specific item in the shopping cart")
    public ShoppingCartDto update(@PathVariable Long id, @RequestBody @Valid UpdateItemRequest requestDto, Authentication authentication) {
        return shoppingCartService.updateCartItem(authentication, id, requestDto);
    }

    @DeleteMapping("/deleteItem/{id}")
    @Operation(summary = "Get all books by category",
            description = "Return list of books by similar category")
    public ShoppingCartDto getBooksByCategoryId(@PathVariable Long id, Authentication authentication) {
        return shoppingCartService.deleteCartItem(id, authentication);
    }
}
