package com.corona.apple.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SingleProductResponse implements Serializable {
    String referenceId;
    String name;
    String shortDescription;
    String longDescription;
    String imageUrl;
    String urlSlug;
    LocationResponse location;
    List<String> badges;
    List<TagResponse> tags;
    List<ProductResponse> similarProducts = new ArrayList<>();
}
