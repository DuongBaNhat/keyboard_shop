package com.nhat.keyboard_shop.repository;

import com.nhat.keyboard_shop.domain.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query(value = "select * from orderdetails where order_id = ?", nativeQuery = true)
    List<OrderDetail> findByOrderId(int id);

    @Query(value = "select * from orderdetails where product_id = ?", nativeQuery = true)
    List<OrderDetail> findByProductId(Long id);
}
