package com.example.kata.shoppingcart.model;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Random;

public class OrderIdGenerator implements IOrderIdGenerator {
    @Override
    public String nextId() {
        return "order-" + getYYYYMMDD() + orderSeq();
    }

    private static String orderSeq() {
        return StringUtils.leftPad("" + new Random().nextInt(100000), 10, "0");
    }

    private static String getYYYYMMDD() {
        return LocalDate.now().toString().replace("-", "");
    }
}
