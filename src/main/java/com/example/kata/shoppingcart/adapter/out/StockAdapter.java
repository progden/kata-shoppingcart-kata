package com.example.kata.shoppingcart.adapter.out;

import com.example.kata.shoppingcart.model.Product;
import com.example.kata.shoppingcart.port.out.CheckStockPort;

import java.util.Random;

public class StockAdapter implements CheckStockPort {
    @Override
    public boolean check(Product product, int quantity) {
        if (product.getId() == 1 && quantity <= new Random().nextInt(10))
            return true;
        if (product.getId() == 2 && quantity <= 1)
            return true;
        return false;
    }
}
