package com.corona.apple.dao.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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

  @CreatedDate Date createdAt;

  @LastModifiedDate Date updatedAt;

  @ManyToMany(targetEntity = Tag.class)
  List<Tag> tags;

  Long views = 0l;

  Long accessCount = 0l;

  double popularity;

  @ElementCollection List<String> badges;
}
