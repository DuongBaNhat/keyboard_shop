package com.nhat.keyboard_shop.repository;

import com.nhat.keyboard_shop.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findByNameContaining(String name, Pageable pageable);


    @Query(value = "select top 1 * from customers where email = ? and status = 1", nativeQuery = true)
    Optional<Customer> FindByEmail(String email);

    @Modifying
    @Query(value = "update Customer u set u.status = false where u.customerId = ?1")
    void SetStatus(int id);

}
