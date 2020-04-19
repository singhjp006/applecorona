package com.corona.apple.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull @NaturalId
    String referenceId;

    @NotNull
    String name;

    String description;

    @NotNull @URL
    String url;

    @URL
    String imageUrl;

    @NotNull @ManyToOne
    Location location;

    String developedBy;

    Long clickCount;

    @NotNull
    Date createdAt;

    Date updatedAt;

    @ManyToMany(targetEntity = Tag.class)
    List<Tag> tags;
}
