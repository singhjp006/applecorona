package com.corona.apple.dao.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Entity(name = "product_click")
@Data
public class ProductClick extends Click {

    @JsonIgnore
  @ToString.Exclude
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "productClick")
  Product product;
}
