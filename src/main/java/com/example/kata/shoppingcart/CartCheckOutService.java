package com.example.kata.shoppingcart;

import com.example.kata.shoppingcart.adapter.out.DiscountAdpater;
import com.example.kata.shoppingcart.adapter.out.StockAdapter;
import com.example.kata.shoppingcart.adapter.out.ShoppingCartAdapter;
import com.example.kata.shoppingcart.adapter.out.OrderRepository;
import com.example.kata.shoppingcart.model.*;
import com.example.kata.shoppingcart.port.in.CartCheckOutUseCase;
import com.example.kata.shoppingcart.port.out.CheckDiscountPort;
import com.example.kata.shoppingcart.port.out.CheckStockPort;
import com.example.kata.shoppingcart.port.out.GetShoppingCartPort;
import com.example.kata.shoppingcart.port.out.SaveOrderPort;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.List;

public class CartCheckOutService implements CartCheckOutUseCase {

    private final GetShoppingCartPort shoppingCartPort;
    private final CheckStockPort checkStockPort;
    private final CheckDiscountPort checkDiscountPort;

    public CartCheckOutService(GetShoppingCartPort shoppingCartPort, CheckStockPort checkStockPort, CheckDiscountPort checkDiscountPort) {
        this.shoppingCartPort = shoppingCartPort;
        this.checkStockPort = checkStockPort;
        this.checkDiscountPort = checkDiscountPort;
    }

    public CartCheckOutService() {
        shoppingCartPort = new ShoppingCartAdapter();
        checkStockPort = new StockAdapter();
        checkDiscountPort = new DiscountAdpater();
    }

    @Override
    public CheckOutResp checkOut(String cartId) {
        // validate cart id
        if (cartId == null || cartId.isEmpty()) {
            return CheckOutResp.error("購物車資訊無效");
        }

        // get cart content by id
        ShoppingCart cart = shoppingCartPort.get(cartId);
        if (cart.getCartItems().size() == 0)
            return CheckOutResp.error("購物車內無商品");

        // check product availability

        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            if (!checkStockPort.check(product, quantity)) {
                throw new RuntimeException(MessageFormat.format("Product {0}-{1} out of stock", product.getId(), product.getName()));
            }
        }

        // check discount availability
        List<Discount> discounts = cart.getDiscount();
        for (Discount discount : discounts) {
            if (!checkDiscountPort.isAvailable(discount)) {
                throw new RuntimeException(MessageFormat.format("Discount code {0}-{1} is invalid", discount.getCode(), discount.getDescription()));
            }
        }

        // calculate total price by product price and quantity and discount in cart
        var totalAmt = cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .reduce(0, Integer::sum);

        var totalDiscount = discounts.stream()
                .map(discount -> discount.getDiscountAmt())
                .reduce(0, Integer::sum);

        var totalPrice = totalAmt - totalDiscount;
        if (totalPrice <= 0)
            totalPrice = 0;

        // create order & save order
        OrderIdGenerator orderIdGenerator = new OrderIdGenerator();

        Order order = new Order();
        order.setId(orderIdGenerator.nextId());
        order.setProducts(cartItems.stream().map(OrderMapper::getOrderItem).toList());
        order.setDiscounts(discounts.stream().map(OrderMapper::getOrderDiscount).toList());
        order.setOrderAmt(totalPrice);

        // save order
        SaveOrderPort orderRepository = new OrderRepository();
        orderRepository.createOrder(order);


        return CheckOutResp.success(order);
    }

    @Getter
    public static class CheckOutResp {
        private boolean isError = false;
        private String message;
        private Order order;

        public CheckOutResp(String message) {
            this.isError = true;
            this.message = message;
        }

        public CheckOutResp(String message, Order order) {
            this.order = order;
            this.message = message;
        }

        public static CheckOutResp error(String message) {
            return new CheckOutResp(message);
        }

        public static CheckOutResp success(Order order) {
            return new CheckOutResp("訂單建立成功", order);
        }
    }

}
