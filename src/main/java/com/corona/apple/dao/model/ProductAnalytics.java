package com.corona.apple.dao.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class ProductAnalytics {
    @Id
    Long id;
    Long views;
    Long accessCount;
    Double popularity;
    Date date;
}
