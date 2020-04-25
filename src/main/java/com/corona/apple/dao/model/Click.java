package com.corona.apple.dao.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class Click {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  Long count;

  public void increment(){
    this.count++;
  }
}
