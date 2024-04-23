package com.example.kata.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product {
    private int id;
    private String name;
    private int price;
}
