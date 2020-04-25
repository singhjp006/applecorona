package com.corona.apple.dao.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tag implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotNull @NaturalId String referenceId;

  @NotNull String name;

  @NotNull
  @Column(columnDefinition = "BOOLEAN")
  Boolean isActive;

  @NotNull Date createdAt;

  Date updatedAt;

  @OneToOne(cascade = CascadeType.ALL)
  TagClick tagClick;
}
