package com.example.bookstore.repository.order;

import com.example.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserId(Long id, Pageable pageable);

//    List<Order> findAllByUserId(Long id);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
