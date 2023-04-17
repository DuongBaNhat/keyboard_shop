package com.nhat.keyboard_shop.domain.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItem {
    private Long productId;
    private String name;
    private int quantity;
    private double price;
    private Date dateAdd;
}
