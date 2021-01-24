package com.ecommerce.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "shopping_order")
public class ShopOrder {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<CartItem> cartItems;
    private Double totalPrice;
}
