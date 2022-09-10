package com.siewe.pos.repository;

import com.siewe.pos.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUserId(Long id);
    User findByLogin(String login);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.name = ?1")
    List<User> findAllByRole(String roleName);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE ?1 OR u.lastName LIKE ?1")
    List<User> findByKeyword(String keyword);

    @Query("select distinct u from User u JOIN u.role r "
            + "where (u.lastName like ?1 or u.firstName like ?1 or ?1 is null) "
            + "and r.name in ?2 ")
    Page<User> findAll(String login, String[] roles, Pageable pageable);

    @Query("select u from User u "
            + "where u.lastName like ?1 or u.firstName like ?1 or ?1 is null")
    Page<User> findAll(String name, Pageable pageable);

    /*
    @Query("SELECT u FROM User u JOIN u.role r "
            + "WHERE r.name = ?1 and (u.deleted is null or u.deleted = false)")
    Page<User> findByRole(String role, Pageable pageable);*/
}
