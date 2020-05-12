package com.corona.apple.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest implements Serializable {

    String name;
    String shortDescription;
    String longDescription;
    URL url;
    String imageUrl;
    String locationName;
    String developedBy;
    List<String> tags;
    Boolean isActive;
    URL androidAppUrl;
    URL iosAppUrl;
    URL videoUrl;

}
