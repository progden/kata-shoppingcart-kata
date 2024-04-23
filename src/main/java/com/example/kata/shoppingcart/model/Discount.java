package com.example.kata.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Discount {
    private String code;
    private String description;
    private int discountAmt;
}
