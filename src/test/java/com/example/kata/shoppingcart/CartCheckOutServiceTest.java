package com.example.kata.shoppingcart;

import com.example.kata.shoppingcart.model.CartItem;
import com.example.kata.shoppingcart.model.Discount;
import com.example.kata.shoppingcart.model.Product;
import com.example.kata.shoppingcart.model.ShoppingCart;
import com.example.kata.shoppingcart.port.out.CheckStockPort;
import com.example.kata.shoppingcart.port.out.GetShoppingCartPort;
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

    private static CartItem fakeCartItemInCart1;
    private CartCheckOutService.CheckOutResp checkoutRs;

    @BeforeEach
    void setUp() {
    }

    @Test
    void checkout() {
        // given
        givenShoppingCart("cartId-correct");
        givenStockPassItem(fakeCartItemInCart1);

        // when
        checkoutCart("cartId-correct");

        // then
        shouldGetSuccessMessageContains("成功");
        assertThat(checkoutRs.getOrder()).isNotNull();
    }

    @Test
    void checkout_when_id_is_empty_then_return_empty_response() {
        // given
        // when
        var checkoutRs = underTest.checkOut("");
        // then
        assertThat(checkoutRs.isError()).isTrue();
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