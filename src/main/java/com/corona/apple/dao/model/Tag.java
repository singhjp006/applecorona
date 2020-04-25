package com.corona.apple.dao.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tag implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull @NaturalId
    String referenceId;

    @NotNull
    String name;

    @NotNull @Column(columnDefinition = "BOOLEAN")
    Boolean isActive;

    @NotNull
    Date createdAt;

    Date updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    TagClick tagClick;
}
