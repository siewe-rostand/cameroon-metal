package com.siewe.pos.repository;

import com.siewe.pos.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    Product findByName(String productName);


    Product findByProductId(Long id);

    @Query("SELECT  p FROM  Product p "
            + "WHERE  ( p.name like ?1  )"
            + "AND ( p.available = false)")
    Page<Product> findAll(String name, Pageable pageable);

    @Override
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE ?1")
    List<Product> findByKeyword(String keyword);


    @Query("SELECT p FROM Product p WHERE p.name LIKE ?1 AND p.available = true ")
    Page<Product> findByAvailableTrue(String name, Pageable pageable);

    @Query("SELECT  p FROM Product  p WHERE p.available=TRUE ")
    Page<Product> findByAvailableTrue(Pageable pageable);

    @Query("SELECT  p FROM  Product p "
            + "WHERE  p.name like ?1"
            + "AND (p.category.id = ?2 ) "
            + "AND (p.deleted is null or p.deleted = false)")
    Page<Product> findAllByCategoryId(String s, Long id, Pageable pageable);
}
