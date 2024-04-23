package com.example.kata.shoppingcart.adapter.out;

import com.example.kata.shoppingcart.model.Product;
import com.example.kata.shoppingcart.port.out.CheckStockPort;

import java.util.Random;

public class StockAdapter implements CheckStockPort {
    @Override
    public boolean check(Product product, int quantity) {
        if (product.getId() == 55123 && quantity <= new Random().nextInt(10))
            return true;
        if (product.getId() == 62867 && quantity <= 1)
            return true;
        return false;
    }
}
