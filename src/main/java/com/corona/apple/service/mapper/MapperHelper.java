package com.corona.apple.service.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.ProductClick;
import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.model.TagClick;
import com.corona.apple.dto.*;
import com.corona.apple.dto.request.CreateProductRequest;
import org.springframework.data.domain.Page;

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

        productResponse.setTags(toTagsResponse(product.getTags()).getTags());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setUrl(product.getUrl());
        productResponse.setLocation(toLocationResponse(product.getLocation()));
        productResponse.setName(product.getName());
        productResponse.setUrlSlug(getUrlSlug(product.getName(), product.getReferenceId()));
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

  public static List<String> tagsToString(List<Tag> tags) {
    List<String> response = new ArrayList<>();
    tags.stream().forEach(tag -> {
      response.add(tag.getName());
    });
    return response;
  }

  public static TagsResponse toTagsResponse(List<Tag> tagEntities) {
    TagsResponse tagsResponse = new TagsResponse();

    List<TagResponse> tags = new ArrayList<>();

    tagEntities.stream().forEach(tagEntity -> {
      TagResponse tagResponse = new TagResponse();
      tagResponse.setName(tagEntity.getName());
      tagResponse.setReferenceId(tagEntity.getReferenceId());

      tags.add(tagResponse);
    });

    tagsResponse.setTags(tags);
    return tagsResponse;
  }

  public static SingleProductResponse toSingleProductResponse(Product product, List<ProductResponse> similarProducts) {
    SingleProductResponse response = new SingleProductResponse();
    response.setImageUrl(product.getImageUrl());
    response.setLocation(toLocationResponse(product.getLocation()));
    response.setLongDescription(product.getLongDescription());
    response.setName(product.getName());
    response.setReferenceId(product.getReferenceId());
    response.setUrl(product.getUrl());
    response.setVideoEmbedUrl(product.getVideoEmbedUrl());
    response.setUrlSlug(getUrlSlug(product.getName(), product.getReferenceId()));
    response.setShortDescription(product.getShortDescription());
    response.setTags(toTagsResponse(product.getTags()).getTags());
    response.setSimilarProducts(similarProducts);
    return response;
  }

  private static String getUrlSlug(String productName, String referenceId) {
    //TODO: change the name of the method used below. because we are using it here as well with some other purpose
    return getReferenceIdForTagOrLocation(productName) + "-" + referenceId;
  }

  public static LocationsResponse toLocationsResponse(Iterable<Location> locations) {
    LocationsResponse locationsResponse = new LocationsResponse();
    List<LocationResponse> locationResponses = new ArrayList<>();
    locations.forEach(location -> {
      LocationResponse locationResponse = new LocationResponse();
      locationResponse.setReferenceId(location.getReferenceId());
      locationResponse.setName(location.getName());
      locationResponses.add(locationResponse);
    });

    locationsResponse.setLocationResponses(locationResponses);
    return locationsResponse;
  }
}
