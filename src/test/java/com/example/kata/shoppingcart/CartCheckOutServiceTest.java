package com.example.kata.shoppingcart;

import com.example.kata.shoppingcart.model.*;
import com.example.kata.shoppingcart.port.out.CheckDiscountPort;
import com.example.kata.shoppingcart.port.out.CheckStockPort;
import com.example.kata.shoppingcart.port.out.GetShoppingCartPort;
import com.example.kata.shoppingcart.port.out.SaveOrderPort;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

    @Mock
    IOrderIdGenerator orderIdGen;

    @Mock
    private SaveOrderPort saveOrderPort;

    @Captor
    ArgumentCaptor<Order> argOrder;

    private static CartItem fakeCartItemInCart150NTD;
    private static Discount fakeDiscount10NTD;
    private static Discount fakeDiscount1000NTD;
    private CartCheckOutService.CheckOutResp checkoutRs;

    private AbstractThrowableAssert<? extends AbstractThrowableAssert<?, ?>, ?> thrown;

    @BeforeEach
    void setUp() {
        thrown = null;
        fakeCartItemInCart150NTD = new CartItem(new Product(1, "product1", 50), 3);
        fakeDiscount10NTD = new Discount("dis-0001", "discount1", 10);
        fakeDiscount1000NTD = new Discount("dis-0002", "discount2", 1000);
    }

    @Test
    void checkout() {
        // given
        givenShoppingCart("cartId-correct", Arrays.asList(fakeCartItemInCart150NTD), Arrays.asList(fakeDiscount10NTD));
        givenStockPassItem(fakeCartItemInCart150NTD);
        givenDiscountPassItem(fakeDiscount10NTD);
        when(orderIdGen.nextId()).thenReturn("fake-orderid");

        // when
        checkoutCart("cartId-correct");

        // then
        shouldGetSuccessMessageContains("成功");
        assertThat(checkoutRs.getOrder()).isNotNull();
        verify(saveOrderPort, times(1)).createOrder(argOrder.capture());
        assertThat(argOrder.getValue().getId()).isEqualTo("fake-orderid");
        assertThat(argOrder.getValue().getOrderAmt()).isEqualTo(150 - 10);
    }

    @Test
    void checkout_when_discount_gte_amt_then_order_amt_eq_0() {
        // given
        givenShoppingCart("cartId-correct", Arrays.asList(fakeCartItemInCart150NTD), Arrays.asList(fakeDiscount1000NTD));
        givenStockPassItem(fakeCartItemInCart150NTD);
        givenDiscountPassItem(fakeDiscount1000NTD);
        when(orderIdGen.nextId()).thenReturn("fake-orderid");

        // when
        checkoutCart("cartId-correct");

        // then
        shouldGetSuccessMessageContains("成功");
        assertThat(checkoutRs.getOrder()).isNotNull();
        verify(saveOrderPort, times(1)).createOrder(argOrder.capture());
        assertThat(argOrder.getValue().getId()).isEqualTo("fake-orderid");
        assertThat(argOrder.getValue().getOrderAmt()).isEqualTo(0);
    }

    @Test
    void checkout_when_discount_not_avaiable_then_checkout_fail() {
        // given
        givenShoppingCart("cartId-correct", Arrays.asList(fakeCartItemInCart150NTD), Arrays.asList(fakeDiscount10NTD));
        givenStockPassItem(fakeCartItemInCart150NTD);
        givenDiscountNotAvailable(fakeDiscount10NTD);

        // when
        checkoutCartWithException("cartId-correct");

        // then
        shouldCatchExceptionAndContains("invalid");
    }

    @Test
    void checkout_when_cartItem_stock_not_available_then_checkout_fail() {
        // given
        givenShoppingCart("cartId-correct", Arrays.asList(fakeCartItemInCart150NTD), Arrays.asList(fakeDiscount10NTD));
        givenStockNotAvailableItem(fakeCartItemInCart150NTD);

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

    private void givenShoppingCart(String cartId, List<CartItem> cartItems, List<Discount> discounts) {
        when(getShoppingCartPort.get(cartId)).thenReturn(createSuccessCart(cartItems, discounts));
    }

    private static ShoppingCart createSuccessCart(List<CartItem> cartItems, List<Discount> discounts) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.getCartItems().addAll(cartItems);
        shoppingCart.getDiscounts().addAll(discounts);
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