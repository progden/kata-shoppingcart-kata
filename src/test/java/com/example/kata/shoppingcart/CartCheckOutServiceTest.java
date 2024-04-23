package com.example.kata.shoppingcart;

import com.example.kata.shoppingcart.model.CartItem;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class CartCheckOutServiceTest {

    private static CartItem fakeCartItem1;
    @InjectMocks
    private CartCheckOutService underTest;

    @Mock
    GetShoppingCartPort getShoppingCartPort;

    @Mock
    CheckStockPort checkStockPort;

    @BeforeEach
    void setUp() {
    }

    @Test
    void checkout() {
        // given
        when(getShoppingCartPort.get("cartId-correct")).thenReturn(createSuccessCart());
        when(checkStockPort.check(fakeCartItem1.getProduct(), fakeCartItem1.getQuantity())).thenReturn(true);

        // when
        var checkoutRs = underTest.checkOut("cartId-correct");
        // then
        assertThat(checkoutRs.getMessage()).contains("成功");
        assertThat(checkoutRs.isError()).isFalse();
        assertThat(checkoutRs.getOrder()).isNotNull();
    }

    private static ShoppingCart createSuccessCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        var cartItems = shoppingCart.getCartItems();
        fakeCartItem1 = new CartItem(new Product(1, "product1", 50), 3);
        cartItems.add(fakeCartItem1);
        return shoppingCart;
    }

    @Test
    void checkout_when_id_is_empty_then_return_empty_response() {
        // given
        // when
        var checkoutRs = underTest.checkOut("");
        // then
        assertThat(checkoutRs.isError()).isTrue();
    }

    @Nested
    class WhenCheckingOut {
        private final String CART_ID = "CART_ID";

        @BeforeEach
        void setup() {
        }
    }
}