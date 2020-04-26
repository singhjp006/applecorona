package com.corona.apple.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse implements Serializable {

    String referenceId;
    String name;
    String shortDescription;
    String imageUrl;
    LocationResponse location;
    List<String> tags;
    List<String> badges;
}
