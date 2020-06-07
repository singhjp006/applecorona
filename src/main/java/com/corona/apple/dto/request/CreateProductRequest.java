package com.corona.apple.dto.request;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

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
  List<TagRequest> tags;
  Boolean isActive;
  URL androidAppUrl;
  URL iosAppUrl;
  URL videoUrl;
  Long curatorsPoint;
}
