package com.nhat.keyboard_shop.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(columnDefinition = "nvarchar(100) not null")
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(length = 200)
    private String image;

    @Column(columnDefinition = "nvarchar(4000) not null")
    private String desciption;

    @Column(nullable = false)
    private double discount;

    @Temporal(TemporalType.DATE)
    private Date enteredDate;

    @Column(nullable = false)
    private short status;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

//	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//	private Set<OrderDetail> orderDetails;
}
