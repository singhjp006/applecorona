package com.corona.apple.dao.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "tag_click")
public class TagClick extends Click{
    @OneToOne
    Tag tag;
}
