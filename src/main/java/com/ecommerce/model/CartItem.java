package com.ecommerce.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="cartItem")
@Data
public class CartItem {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    ShopOrder order;

    private Integer quantity;

}
