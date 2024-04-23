package com.example.kata.shoppingcart.port.out;

import com.example.kata.shoppingcart.model.Discount;

public interface CheckDiscountPort {
    boolean isAvailable(Discount discount);
}
