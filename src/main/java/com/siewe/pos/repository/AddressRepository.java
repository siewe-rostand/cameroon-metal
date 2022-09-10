package com.siewe.pos.repository;

import com.siewe.pos.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

    Address findByAddressId(Long id);

    @Override
    Page<Address> findAll(Pageable pageable);

    @Query("SELECT a FROM Address a WHERE a.city LIKE ?1")
    List<Address> findByKeyword(String keyword);
}
