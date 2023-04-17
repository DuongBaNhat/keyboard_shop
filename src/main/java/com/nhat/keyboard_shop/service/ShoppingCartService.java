package com.nhat.keyboard_shop.service;

import com.nhat.keyboard_shop.domain.util.CartItem;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface ShoppingCartService {
    int getCount();

    double getAmount();

    void update(Long id, int quantity);

    void clear();

    Collection<CartItem> getCartItems();

    void remove(Long id);

    void add(CartItem item);
}
