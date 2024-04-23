package com.example.kata.shoppingcart.port.out;

import com.example.kata.shoppingcart.model.Order;

public interface SaveOrderPort {
    void createOrder(Order order);
}
