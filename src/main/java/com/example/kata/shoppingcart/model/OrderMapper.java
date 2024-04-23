package com.example.kata.shoppingcart.model;

public class OrderMapper {
    public static Order.OrderItem getOrderItem(CartItem cartItem) {
        return new Order.OrderItem(cartItem.getProduct(), cartItem.getQuantity());
    }

    public static Order.OrderDiscount getOrderDiscount(Discount discount) {
        return new Order.OrderDiscount(discount);
    }
}
