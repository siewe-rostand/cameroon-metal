package com.siewe.pos.repository;

import com.siewe.pos.model.ProductStock;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductStockRepository extends JpaRepository<ProductStock,Long> {
    default ProductStock findOne(Long id) {
        return (ProductStock) findById(id).orElse(null);
    }

    ProductStock findByProductProductIdAndDate(Long productId, LocalDate date);

    ProductStock findFirstByProductProductIdOrderByDateDesc(Long productId);

    List<ProductStock> findByProductProductIdAndDateAfter(Long productId, LocalDate date);

    @Query("SELECT ps FROM  ProductStock ps "
            + "WHERE ps.product.id = ?1 AND ps.date between ?2 and ?3 ")
    List<ProductStock> findByProductIdAndDateRange(Long productId, LocalDate df, LocalDate dt);
}
