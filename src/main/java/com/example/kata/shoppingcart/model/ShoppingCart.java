package com.example.kata.shoppingcart.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ShoppingCart {
    private final ArrayList<CartItem> cartItems = new ArrayList<>();
    private final ArrayList<Discount> discounts = new ArrayList<>();

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public List<Discount> getDiscount() {
        return discounts;
    }
}
