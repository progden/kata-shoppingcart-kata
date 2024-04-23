package com.example.kata.shoppingcart;

import com.example.kata.shoppingcart.model.CartItem;
import com.example.kata.shoppingcart.model.Discount;
import com.example.kata.shoppingcart.model.Product;
import com.example.kata.shoppingcart.model.ShoppingCart;
import com.example.kata.shoppingcart.port.out.CheckDiscountPort;
import com.example.kata.shoppingcart.port.out.CheckStockPort;
import com.example.kata.shoppingcart.port.out.GetShoppingCartPort;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class CartCheckOutServiceTest {

    @InjectMocks
    private CartCheckOutService underTest;

    @Mock
    GetShoppingCartPort getShoppingCartPort;

    @Mock
    CheckStockPort checkStockPort;

    @Mock
    CheckDiscountPort checkDiscountPort;

    private static CartItem fakeCartItemInCart1;
    private static Discount fakeDiscount;
    private CartCheckOutService.CheckOutResp checkoutRs;

    private AbstractThrowableAssert<? extends AbstractThrowableAssert<?, ?>, ?> thrown;

    @BeforeEach
    void setUp() {
        thrown = null;
    }

    @Test
    void checkout() {
        // given
        givenShoppingCart("cartId-correct");
        givenStockPassItem(fakeCartItemInCart1);
        givenDiscountPassItem(fakeDiscount);

        // when
        checkoutCart("cartId-correct");

        // then
        shouldGetSuccessMessageContains("成功");
        assertThat(checkoutRs.getOrder()).isNotNull();
    }

    @Test
    void checkout_when_discount_not_avaiable_then_checkout_fail() {
        // given
        givenShoppingCart("cartId-correct");
        givenStockPassItem(fakeCartItemInCart1);
        givenDiscountNotAvailable(fakeDiscount);

        // when
        checkoutCartWithException("cartId-correct");

        // then
        shouldCatchExceptionAndContains("invalid");
    }

    @Test
    void checkout_when_cartItem_stock_not_available_then_checkout_fail() {
        // given
        givenShoppingCart("cartId-correct");
        givenStockNotAvailableItem(fakeCartItemInCart1);

        // when
        checkoutCartWithException("cartId-correct");

        // then
        shouldCatchExceptionAndContains("stock");
    }

    @Test
    void checkout_when_id_is_empty_then_return_empty_response() {
        // given
        // when
        var checkoutRs = underTest.checkOut("");
        // then
        assertThat(checkoutRs.isError()).isTrue();
    }

    private void givenDiscountPassItem(Discount... discounts) {
        Arrays.stream(discounts).forEach(fakeDiscount -> {
            when(checkDiscountPort.isAvailable(fakeDiscount)).thenReturn(true);
        });
    }

    private void givenDiscountNotAvailable(Discount... discounts) {
        Arrays.stream(discounts).forEach(fakeDiscount -> {
            when(checkDiscountPort.isAvailable(fakeDiscount)).thenReturn(false);
        });
    }

    private void shouldCatchExceptionAndContains(String message) {
        thrown.isInstanceOf(RuntimeException.class)
                .hasMessageContaining(message);
        assertThat(checkoutRs).isNull();
    }

    private void givenStockNotAvailableItem(CartItem... items) {
        Arrays.stream(items).forEach(fakeCartItem1 -> {
            when(checkStockPort.check(fakeCartItem1.getProduct(), fakeCartItem1.getQuantity())).thenReturn(false);
        });
    }

    private void shouldGetSuccessMessageContains(String msgStr) {
        assertThat(checkoutRs.isError()).isFalse();
        assertThat(checkoutRs.getMessage()).contains(msgStr);
    }

    private void shouldGetFailMessageContains(String msgStr) {
        assertThat(checkoutRs.isError()).isTrue();
        assertThat(checkoutRs.getMessage()).contains(msgStr);
    }

    private void checkoutCartWithException(String cartId) {
        thrown = assertThatThrownBy(() -> {
            checkoutCart(cartId);
        });
    }

    private void checkoutCart(String cartId) {
        checkoutRs = underTest.checkOut(cartId);
    }

    private void givenStockPassItem(CartItem... cartItems) {
        Arrays.stream(cartItems).forEach(fakeCartItem1 -> {
            when(checkStockPort.check(fakeCartItem1.getProduct(), fakeCartItem1.getQuantity())).thenReturn(true);
        });
    }

    private void givenShoppingCart(String cartId) {
        when(getShoppingCartPort.get(cartId)).thenReturn(createSuccessCart());
    }

    private static ShoppingCart createSuccessCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        var cartItems = shoppingCart.getCartItems();
        fakeCartItemInCart1 = new CartItem(new Product(1, "product1", 50), 3);
        cartItems.add(fakeCartItemInCart1);

        var discounts = shoppingCart.getDiscounts();
        fakeDiscount = new Discount("dis-0001", "discount1", 10);
        discounts.add(fakeDiscount);
        return shoppingCart;
    }

    @Nested
    class WhenCheckingOut {
        private final String CART_ID = "CART_ID";

        @BeforeEach
        void setup() {
        }
    }
}