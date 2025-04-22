package com.example.bookstore.service.impl;

import com.example.bookstore.config.DateTimeConfig;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PostOrderRequestDto;
import com.example.bookstore.dto.order.PutOrderRequest;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto placeOrder(Authentication authentication, PostOrderRequestDto dto) {
        User user = getCurrentUser(authentication);
        ShoppingCart shoppingCart = getCurrentShoppingCart(user.getId());
        Order order = createOrder(user, shoppingCart, dto);
        cartItemRepository.deleteAll(shoppingCart.getCartItems());
        return orderMapper.toDto(order, orderItemMapper);
    }

    @Override
    public List<OrderDto> getAll(Authentication authentication, Pageable pageable) {
        User user = getCurrentUser(authentication);
        return orderRepository.findAllByUserId(user.getId(), pageable).stream()
                .map(order -> orderMapper.toDto(order, orderItemMapper))
                .toList();
    }

    @Override
    public List<OrderItemDto> getAllOrderItems(Authentication authentication,
                                               Long orderId, Pageable pageable) {
        User user = getCurrentUser(authentication);
        Order order = getOrder(orderId, user.getId());
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto getOrderById(Authentication authentication, Long orderId) {
        User user = getCurrentUser(authentication);
        return orderMapper.toDto(getOrder(orderId, user.getId()), orderItemMapper);
    }

    @Override
    public OrderDto updateStatus(Authentication authentication,
                                 Long orderId, PutOrderRequest dto) {
        User user = getCurrentUser(authentication);
        Order order = getOrder(orderId, user.getId());
        Order.Status status = Order.Status.valueOf(dto.getStatus().toUpperCase());
        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order), orderItemMapper);
    }

    @Override
    public OrderItemDto getOrderItemBy(Authentication authentication,
                                       Long orderId, Long orderItemId) {
        User user = getCurrentUser(authentication);
        List<OrderItemDto> allOrderItems = getAllOrderItems(authentication,
                orderId, Pageable.unpaged());
        return allOrderItems.stream()
                .filter(item -> item != null && item.getId().equals(orderItemId))
                .findFirst().orElseThrow(() ->
                        new EntityNotFoundException("Can't find item with id: " + orderItemId));
    }

    private Order createOrder(User user, ShoppingCart shoppingCart,
                              PostOrderRequestDto dto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.NEW);
        order.setTotal(countTotal(shoppingCart));
        order.setShippingAddress(dto.getShippingAddress());
        order.setOrderDate(LocalDateTime.now().format(DateTimeConfig.format));
        orderRepository.save(order);
        order.setOrderItems(getOrderItems(shoppingCart, order));
        return order;
    }

    private Set<OrderItem> getOrderItems(ShoppingCart shoppingCart, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setBook(cartItem.getBook());
            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found"));
    }

    private ShoppingCart getCurrentShoppingCart(Long id) {
        return shoppingCartRepository.findByUserId(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart with id: " + id));
    }

    private Order getOrder(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by this id: " + orderId));
    }

    private BigDecimal countTotal(ShoppingCart shoppingCart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            Book book = cartItem.getBook();
            BigDecimal cartItemPrice = book.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(cartItemPrice);
        }
        return total;
    }
}
