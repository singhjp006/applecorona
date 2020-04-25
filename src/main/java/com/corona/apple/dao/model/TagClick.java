package com.corona.apple.dao.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Entity(name = "tag_click")
@Data
public class TagClick extends Click {
  @JsonIgnore
  @OneToOne(mappedBy = "tagClick")
  @ToString.Exclude

  Tag tag;
}
