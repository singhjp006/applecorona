package com.corona.apple.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse implements Serializable {

  String referenceId;
  String name;
  String shortDescription;
  String url;
  String imageUrl;
  String urlSlug;
  LocationResponse location;
  List<TagResponse> tags;
  List<String> badges;
  // TODO: 30/04/20 for testing only
  double popularity;
}
