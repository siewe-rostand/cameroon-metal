package com.siewe.pos.repository;

import com.siewe.pos.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByOrderId(Long id);
    @Query("SELECT  o FROM Order o WHERE (o.orderId LIKE ?1 AND o.totalAmount LIKE ?1 OR ?1 IS NULL)")
    Page<Order> findAll(String name, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount LIKE ?1 OR o.customers LIKE ?1")
    List<Order> findByKeyword(String keyword);
}
