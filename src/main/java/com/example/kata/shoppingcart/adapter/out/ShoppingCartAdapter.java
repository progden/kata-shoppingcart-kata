package com.example.kata.shoppingcart.adapter.out;

import com.example.kata.shoppingcart.model.CartItem;
import com.example.kata.shoppingcart.model.Discount;
import com.example.kata.shoppingcart.model.Product;
import com.example.kata.shoppingcart.model.ShoppingCart;
import com.example.kata.shoppingcart.port.out.GetShoppingCartPort;

public class ShoppingCartAdapter implements GetShoppingCartPort {
    @Override
    public ShoppingCart get(String cartId) {
        if (cartId.equals("cartId")) {
            ShoppingCart shoppingCart = new ShoppingCart();
            var cartItems = shoppingCart.getCartItems();
            cartItems.add(new CartItem(new Product(55123, "product1", 50), 3));
            cartItems.add(new CartItem(new Product(62867, "product2", 150), 1));
            var discounts = shoppingCart.getDiscounts();
            discounts.add(new Discount("DFEKX", "discount1", 10));
            return shoppingCart;
        }
        return new ShoppingCart();
    }
}
