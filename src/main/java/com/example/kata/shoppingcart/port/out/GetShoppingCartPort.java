package com.example.kata.shoppingcart.port.out;

import com.example.kata.shoppingcart.model.ShoppingCart;

public interface GetShoppingCartPort {
    ShoppingCart get(String cartId);
}
