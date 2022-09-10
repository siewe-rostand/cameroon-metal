package com.siewe.pos.repository;

import com.siewe.pos.model.OrderedProduct;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long> {
    default OrderedProduct findOne(Long id) {
        return (OrderedProduct) findById(id).orElse(null);
    }

    OrderedProduct findByOrder_OrderIdAndProductProductId(Long orderId, Long productId);

    List<OrderedProduct> findByOrder_OrderId(Long orderId);

    @Query("SELECT op FROM  OrderedProduct op "
            + "WHERE op.product.id = ?1 "
            + "AND op.order.createdDate between ?2 and ?3 ")
    List<OrderedProduct> findByProductIdAndCreatedDateBetween(Long productId, LocalDateTime df, LocalDateTime dt);

    @Query("SELECT op FROM  OrderedProduct op "
            + "WHERE op.product.id = ?1 "
            + "AND op.order.user.id = ?2 "
            + "AND op.order.createdDate between ?3 and ?4 ")
    List<OrderedProduct> findByProductIdAndSellerIdCreatedDateBetween(Long productId, Long sellerId, LocalDateTime d, LocalDateTime localDateTime);
}
