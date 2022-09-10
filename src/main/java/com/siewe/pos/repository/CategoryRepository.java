package com.siewe.pos.repository;

import com.siewe.pos.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryId(Long id);
    @Query("SELECT  c FROM  Category c "
            + "WHERE c.name = ?1" )
    Category findByName(String name);


    List<Category> findByEnabledTrue();
}
