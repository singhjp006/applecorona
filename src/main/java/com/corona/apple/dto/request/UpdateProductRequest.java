package com.corona.apple.dto.request;

import java.io.Serializable;
import java.net.URL;

import com.corona.apple.Badges;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest implements Serializable {

  Long id;
  String name;
  String shortDescription;
  String longDescription;
  URL url;
  String imageS3Url;
  String locationName;
  String developedBy;
  Boolean isActive;
  URL androidAppUrl;
  URL iosAppUrl;
  URL videoUrl;
  Long curatorsPoint;
  Badges badge;
}
