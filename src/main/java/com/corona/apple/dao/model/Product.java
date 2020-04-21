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

    String shortDescription;

    String longDescription;

    @NotNull @URL
    String url;

    @URL
    String imageUrl;

    @NotNull @ManyToOne
    Location location;

    String developedBy;

    @NotNull
    Long clickCount;

    @NotNull @Column(columnDefinition = "BOOLEAN")
    Boolean isActive;

    @NotNull
    Date createdAt;

    Date updatedAt;

    @ManyToMany(targetEntity = Tag.class)
    List<Tag> tags;
}
