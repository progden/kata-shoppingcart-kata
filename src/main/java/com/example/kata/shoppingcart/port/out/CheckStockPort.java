package com.example.kata.shoppingcart.port.out;

import com.example.kata.shoppingcart.model.Product;

public interface CheckStockPort {
    boolean check(Product product, int quantity);
}
