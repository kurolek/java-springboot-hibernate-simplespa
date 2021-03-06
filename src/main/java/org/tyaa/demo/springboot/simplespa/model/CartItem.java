package org.tyaa.demo.springboot.simplespa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/* Товар в корзине */
@AllArgsConstructor
@Getter
@Setter
public class CartItem {
    public enum Action {
        ADD // добавить один пункт товара в корзину
        , SUB // убрать один пункт товара из корзины
        , REM // убрать все пункты товара из корзины
    }
    private Long id;
    private String name;
    private Integer count;
}
