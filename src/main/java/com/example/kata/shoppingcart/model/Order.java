package com.example.kata.shoppingcart.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class Order {
    private String id;
    private List<OrderItem> products;
    private List<OrderDiscount> discounts;
    private int orderAmt;


    @Getter
    public static class OrderItem {
        private Product product;
        private int quantity;

        public OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    @Getter
    public static class OrderDiscount {
        private String code;
        private String description;

        public OrderDiscount(Discount discount) {
            this.code = discount.getCode();
            this.description = discount.getDescription();
        }
    }
}
