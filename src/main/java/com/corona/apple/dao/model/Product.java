package com.corona.apple.dao.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.URL;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotNull @NaturalId String referenceId;

  @NotNull String name;

  String shortDescription;

  String longDescription;

  @NotNull @URL String url;

  @URL String imageUrl;

  @URL String videoEmbedUrl;

  @URL String androidAppUrl;

  @URL String iosAppUrl;

  @NotNull @JsonIgnore @ManyToOne Location location;

  String developedBy;

  @NotNull
  @Column(columnDefinition = "BOOLEAN")
  Boolean isActive;

  @NotNull Date createdAt;

  Date updatedAt;

  @ManyToMany(targetEntity = Tag.class)
  List<Tag> tags;

  @OneToOne(cascade = CascadeType.ALL)
  ProductClick productClick;
}
