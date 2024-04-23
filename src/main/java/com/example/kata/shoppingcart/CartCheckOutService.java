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
import java.util.function.Consumer;

public class CartCheckOutService implements CartCheckOutUseCase {
    private final GetShoppingCartPort shoppingCartPort;
    private final CheckStockPort checkStockPort;
    private final CheckDiscountPort checkDiscountPort;
    private final IOrderIdGenerator orderIdGenerator;
    private final SaveOrderPort orderRepository;

    public CartCheckOutService(GetShoppingCartPort shoppingCartPort, CheckStockPort checkStockPort, CheckDiscountPort checkDiscountPort, IOrderIdGenerator orderIdGenerator, SaveOrderPort saveOrderPort) {
        this.shoppingCartPort = shoppingCartPort;
        this.checkStockPort = checkStockPort;
        this.checkDiscountPort = checkDiscountPort;
        this.orderRepository = saveOrderPort;
        this.orderIdGenerator = orderIdGenerator;
    }

    public CartCheckOutService() {
        shoppingCartPort = new ShoppingCartAdapter();
        checkStockPort = new StockAdapter();
        checkDiscountPort = new DiscountAdpater();
        orderRepository = new OrderRepository();
        orderIdGenerator = new OrderIdGenerator();
    }

    @Override
    public CheckOutResp checkOut(String cartId) {
        // validate cart id
        if (isEmptyCartId(cartId)) {
            return CheckOutResp.error("購物車資訊無效");
        }

        // get cart content by id
        ShoppingCart cart = shoppingCartPort.get(cartId);
        if (isEmptyCart(cart))
            return CheckOutResp.error("購物車內無商品");

        // check product availability
        checkProductStocks(cart, /* if cart item not available */cartItem -> {
            throw new RuntimeException(MessageFormat.format("Product {0}-{1} out of stock", cartItem.getProduct().getId(), cartItem.getProduct().getName()));
        });

        // check discount availability
        checkDiscountAvailable(cart, /* if discount code been used */ discount -> {
            throw new RuntimeException(MessageFormat.format("Discount code {0}-{1} is invalid", discount.getCode(), discount.getDescription()));
        });

        // create order & save order
        Order order = createOrder(cart, orderIdGenerator.nextId());

        // save order
        orderRepository.createOrder(order);

        return CheckOutResp.success(order);
    }

    private Order createOrder(ShoppingCart cart, String id) {
        Order order = new Order();
        order.setId(id);
        order.setProducts(cart.getCartItems().stream().map(OrderMapper::getOrderItem).toList());
        order.setDiscounts(cart.getDiscount().stream().map(OrderMapper::getOrderDiscount).toList());
        // calculate total price by product price and quantity and discount in cart
        order.setOrderAmt(totalPrice(cart));
        return order;
    }

    private static int totalPrice(ShoppingCart cart) {
        var totalPrice = productPrice(cart) - discountAmt(cart);
        if (totalPrice <= 0) totalPrice = 0;
        return totalPrice;
    }

    private static Integer discountAmt(ShoppingCart cart) {
        return cart.getDiscount().stream()
                .map(discount -> discount.getDiscountAmt())
                .reduce(0, Integer::sum);
    }

    private static Integer productPrice(ShoppingCart cart) {
        return cart.getCartItems().stream()
                .map(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .reduce(0, Integer::sum);
    }

    private void checkDiscountAvailable(ShoppingCart cart, Consumer<Discount> isDiscountNotAvaiable) {
        cart.getDiscounts().forEach(discount -> {
            if (!checkDiscountPort.isAvailable(discount)) {
                isDiscountNotAvaiable.accept(discount);
                return;
            }
        });
    }

    private void checkProductStocks(ShoppingCart cart, Consumer<CartItem> ifNoStock) {
        cart.getCartItems().forEach(cartItem -> {
            if (!checkStockPort.check(cartItem.getProduct(), cartItem.getQuantity())) {
                ifNoStock.accept(cartItem);
            }
        });
    }

    private static boolean isEmptyCart(ShoppingCart cart) {
        return cart.getCartItems().size() == 0;
    }

    private static boolean isEmptyCartId(String cartId) {
        return cartId == null || cartId.isEmpty();
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
