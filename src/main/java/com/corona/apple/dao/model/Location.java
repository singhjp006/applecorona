package com.corona.apple.dao.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotNull @NaturalId String referenceId;

  @NotNull String name;

  @NotNull
  @Column(columnDefinition = "BOOLEAN")
  Boolean isActive;

  @ToString.Exclude
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "location", targetEntity = Product.class)
  List<Product> products;
}
