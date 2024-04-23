package com.example.kata.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;
}
