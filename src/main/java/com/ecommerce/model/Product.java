package com.ecommerce.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer bookID;
    @Lob
    String title;
    @Lob
    String authors;
    Integer ratingsCount;
    String language;
    String isbn;
    Double averageRating;
    Double price;
    String image;
}
