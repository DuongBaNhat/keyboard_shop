package com.nhat.keyboard_shop.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private short status;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

//	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//	private Set<OrderDetail> orderDetails;
}
