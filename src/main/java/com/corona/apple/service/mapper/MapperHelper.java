package com.corona.apple.service.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.ProductClick;
import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.model.TagClick;
import com.corona.apple.dto.LocationResponse;
import com.corona.apple.dto.PaginationResponse;
import com.corona.apple.dto.ProductResponse;
import com.corona.apple.dto.ProductsResponse;
import com.corona.apple.dto.request.CreateProductRequest;
import org.springframework.data.domain.Page;
import sun.awt.X11.XPropertyEvent;

public class MapperHelper {

  private MapperHelper() {}

  public static Product toProduct(
      CreateProductRequest createProductRequest,
      String imageS3Url,
      List<Tag> tags,
      Location location) {
    Product productEntity = new Product();
    productEntity.setCreatedAt(new Date());
    productEntity.setDevelopedBy(createProductRequest.getDevelopedBy());
    productEntity.setImageUrl(imageS3Url);
    productEntity.setIsActive(createProductRequest.getIsActive());
    productEntity.setShortDescription(createProductRequest.getShortDescription().trim());
    productEntity.setLongDescription(createProductRequest.getLongDescription().trim());
    productEntity.setName(createProductRequest.getName().trim());
    productEntity.setReferenceId(getReferenceIdForProduct(createProductRequest.getName().trim()));
    productEntity.setTags(tags);
    productEntity.setUrl(createProductRequest.getUrl());
    productEntity.setLocation(location);
    productEntity.setProductClick(getProductClick(productEntity));

    return productEntity;
  }

  private static String getReferenceIdForProduct(String productName) {

    final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder builder = new StringBuilder();
    int count = 12;
    while (count-- != 0) {
      int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    return builder.toString();
  }

  public static Tag toTag(String name) {
    Tag tagEntity = new Tag();
    tagEntity.setCreatedAt(new Date());
    tagEntity.setIsActive(true);
    tagEntity.setName(name.trim());
    tagEntity.setReferenceId(getReferenceIdForTagOrLocation(name));
    tagEntity.setTagClick(getTagClick(tagEntity));
    return tagEntity;
  }

  private static TagClick getTagClick(Tag tag) {
    TagClick tagClick = new TagClick();
    tagClick.setTag(tag);
    return tagClick;
  }

  private static String getReferenceIdForTagOrLocation(String name) {
    return name.trim().replaceAll("\\s+", "-").toLowerCase();
  }

  public static Location toLocation(String locationName) {
    Location location = new Location();
    location.setIsActive(true);
    location.setName(locationName.trim());
    location.setReferenceId(getReferenceIdForTagOrLocation(locationName));
    return location;
  }

  private static ProductClick getProductClick(Product product) {

    ProductClick productClick = new ProductClick();
    productClick.setProduct(product);
    return productClick;
  }

  public static ProductsResponse toProductsResponse(Page<Product> products, Long limit, Long offset) {
    ProductsResponse response = new ProductsResponse();

    PaginationResponse pagination = new PaginationResponse();
    pagination.setLimit(limit);
    pagination.setOffset(offset);
    pagination.setTotal(products.getTotalElements());
    response.setPagination(pagination);

    List<ProductResponse> productsResponse = new ArrayList<>();

    products.get().forEach(product -> {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setTags(tagsToString(product.getTags()));
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setLocation(toLocationResponse(product.getLocation()));
        productResponse.setName(product.getName());
        productResponse.setReferenceId(product.getReferenceId());
        productResponse.setShortDescription(product.getShortDescription());

        productsResponse.add(productResponse);
    });

    response.setProducts(productsResponse);

    return response;
  }

  private static LocationResponse toLocationResponse(Location location) {
    LocationResponse response = new LocationResponse();
    response.setName(location.getName());
    response.setReferenceId(location.getReferenceId());
    return response;
  }

  private static List<String> tagsToString(List<Tag> tags) {
    List<String> response = new ArrayList<>();
    tags.stream().forEach(tag -> {
      response.add(tag.getName());
    });
    return response;
  }
}
