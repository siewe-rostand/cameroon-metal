package com.siewe.pos.repository;

import com.siewe.pos.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByCustomerId(Long id);

    @Query("SELECT  s FROM Customer s WHERE (s.lastName LIKE ?1 AND s.firstName LIKE ?1 OR ?1 IS NULL)")
    Page<Customer> findAll(String name, Pageable pageable);

    @Query("SELECT s FROM Customer s WHERE s.firstName LIKE ?1 OR s.lastName LIKE ?1")
    List<Customer> findByKeyword(String keyword);

    Customer findByFirstNameAndLastName(String firstName,String lastName);
}
