package com.corona.apple.dao.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity(name = "product_click")
@Data
public class ProductClick extends Click{
    @OneToOne
    Product product;
}
