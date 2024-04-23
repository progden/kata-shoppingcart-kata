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
            cartItems.add(new CartItem(new Product(1, "product1", 50), 3));
            cartItems.add(new CartItem(new Product(2, "product2", 150), 1));
            var discounts = shoppingCart.getDiscounts();
            discounts.add(new Discount("dis-0001", "discount1", 10));
            return shoppingCart;
        }
        return new ShoppingCart();
    }
}
