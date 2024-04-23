package com.example.kata.shoppingcart.port.in;

import com.example.kata.shoppingcart.CartCheckOutService;

public interface CartCheckOutUseCase {
    CartCheckOutService.CheckOutResp checkOut(String cartId);
}
