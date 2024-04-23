package com.example.kata.shoppingcart.adapter.out;

import com.example.kata.shoppingcart.model.Discount;
import com.example.kata.shoppingcart.port.out.CheckDiscountPort;

public class DiscountAdpater implements CheckDiscountPort {
    @Override
    public boolean isAvailable(Discount discount) {
        if (discount.getCode().equals("DFEKX")) return true;
        return false;
    }
}
