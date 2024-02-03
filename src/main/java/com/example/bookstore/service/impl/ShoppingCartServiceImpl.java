package com.example.bookstore.service.impl;

import com.example.bookstore.dto.cart.AddItemToCartRequest;
import com.example.bookstore.dto.cart.ShoppingCartDto;
import com.example.bookstore.dto.cart.UpdateItemRequest;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.ShoppingCartService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto addToCart(Authentication authentication, AddItemToCartRequest dto) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email: " + email));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    newShoppingCart.setUser(user);
                    shoppingCartRepository.save(newShoppingCart);
                    return newShoppingCart;
                });

        CartItem cartItem = createCartItem(dto, shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private CartItem createCartItem(AddItemToCartRequest dto, ShoppingCart shoppingCart) {
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find book by id: " + dto.getBookId()));
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(dto.getQuantity());
        cartItem.setShoppingCart(shoppingCart);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public ShoppingCartDto getByUser(Authentication authentication) {
        return shoppingCartMapper.toDto(getShoppingCart(authentication));
    }

    @Override
    public ShoppingCartDto updateCartItem(Authentication authentication,
                                          Long itemId, UpdateItemRequest dto) {
        CartItem cartItem = cartItemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Can't find cartItem by id: " + itemId));
        cartItem.setQuantity(dto.getQuantity());
        cartItemRepository.save(cartItem);
        ShoppingCart shoppingCart = getShoppingCart(authentication);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto deleteCartItem(Long itemId, Authentication authentication) {
        cartItemRepository.deleteById(itemId);
        ShoppingCart shoppingCart = getShoppingCart(authentication);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private ShoppingCart getShoppingCart(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by email: " + email));
        return shoppingCartRepository.findByUserId(user.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find Shopping cart by id: " + user.getId()));
    }
}
