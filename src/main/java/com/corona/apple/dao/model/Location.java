package com.corona.apple.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull @NaturalId
    String referenceId;

    @NotNull
    String locationName;

    @OneToMany(mappedBy = "location", targetEntity = Product.class)
    List<Product> products;
}
