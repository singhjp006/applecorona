package com.corona.apple.service.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.corona.apple.Badges;
import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.Tag;
import com.corona.apple.dto.LocationResponse;
import com.corona.apple.dto.LocationsResponse;
import com.corona.apple.dto.PaginatedResponse;
import com.corona.apple.dto.ProductResponse;
import com.corona.apple.dto.SingleProductResponse;
import com.corona.apple.dto.TagResponse;
import com.corona.apple.dto.TagsResponse;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.dto.request.TagRequest;
import com.corona.apple.dto.request.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public class MapperHelper {

  private MapperHelper() {}

  public static void toProduct(UpdateProductRequest updateProductRequest, Product productEntity) {

    productEntity.setUpdatedAt(new Date());
    if (Objects.nonNull(updateProductRequest.getDevelopedBy())) {
      productEntity.setDevelopedBy(updateProductRequest.getDevelopedBy());
    }
    if (Objects.nonNull(updateProductRequest.getImageS3Url())) {
      productEntity.setImageUrl(updateProductRequest.getImageS3Url());
    }
    if (Objects.nonNull(updateProductRequest.getIsActive())) {
      productEntity.setIsActive(updateProductRequest.getIsActive());
    }
    if (Objects.nonNull(updateProductRequest.getShortDescription())) {
      productEntity.setShortDescription(updateProductRequest.getShortDescription().trim());
    }
    if (Objects.nonNull(updateProductRequest.getLongDescription())) {
      productEntity.setLongDescription(updateProductRequest.getLongDescription().trim());
    }
    if (Objects.nonNull(updateProductRequest.getName())) {
      productEntity.setName(updateProductRequest.getName().trim());
    }
    if (Objects.nonNull(updateProductRequest.getUrl())) {
      productEntity.setUrl(updateProductRequest.getUrl().toString());
    }
    if (updateProductRequest.getVideoUrl() != null) {
      productEntity.setVideoEmbedUrl(updateProductRequest.getVideoUrl().toString());
    }
    if (updateProductRequest.getAndroidAppUrl() != null) {
      productEntity.setAndroidAppUrl(updateProductRequest.getAndroidAppUrl().toString());
    }
    if (updateProductRequest.getIosAppUrl() != null) {
      productEntity.setIosAppUrl(updateProductRequest.getIosAppUrl().toString());
    }

    if (Objects.nonNull(updateProductRequest.getCuratorsPoint())) {
      productEntity.setCuratorsPoint(updateProductRequest.getCuratorsPoint());
    }

    if (Objects.nonNull(updateProductRequest.getBadge())) {
      List<String> badges = productEntity.getBadges();
      badges.add(updateProductRequest.getBadge().name());
      productEntity.setBadges(badges.stream().distinct().collect(Collectors.toList()));
    }
  }

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
    productEntity.setUrl(createProductRequest.getUrl().toString());
    if (createProductRequest.getVideoUrl() != null) {
      productEntity.setVideoEmbedUrl(createProductRequest.getVideoUrl().toString());
    }
    if (createProductRequest.getAndroidAppUrl() != null) {
      productEntity.setAndroidAppUrl(createProductRequest.getAndroidAppUrl().toString());
    }
    if (createProductRequest.getIosAppUrl() != null) {
      productEntity.setIosAppUrl(createProductRequest.getIosAppUrl().toString());
    }

    productEntity.setUpdatedAt(new Date());
    productEntity.setLocation(location);
    productEntity.setCuratorsPoint(createProductRequest.getCuratorsPoint());
    productEntity.setBadges(Collections.singletonList(Badges.NEW.name()));

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

  public static Tag toTag(TagRequest tagRequest) {
    Tag tagEntity = new Tag();
    tagEntity.setCreatedAt(new Date());
    tagEntity.setIsActive(true);
    tagEntity.setName(tagRequest.getTagName().trim());
    tagEntity.setCategory(tagRequest.isCategory());
    tagEntity.setReferenceId(getReferenceIdForTagOrLocation(tagRequest.getTagName()));
    return tagEntity;
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

  public static PaginatedResponse<ProductResponse> toProductResponses(
      Page<Product> products, Long limit, Long offset) {

    List<ProductResponse> productsResponse = new ArrayList<>();

    products
        .get()
        .forEach(
            product -> {
              ProductResponse productResponse = new ProductResponse();

              productResponse.setTags(toTagsResponse(product.getTags()).getTags());
              productResponse.setImageUrl(product.getImageUrl());
              productResponse.setUrl(product.getUrl());
              productResponse.setLocation(toLocationResponse(product.getLocation()));
              productResponse.setName(product.getName());
              productResponse.setUrlSlug(getUrlSlug(product.getName(), product.getReferenceId()));
              productResponse.setReferenceId(product.getReferenceId());
              productResponse.setShortDescription(product.getShortDescription());
              productResponse.setPopularity(product.getPopularity());

              productsResponse.add(productResponse);
            });

    return PaginatedResponse.from(productsResponse, products.getTotalElements(), offset, limit);
  }

  private static LocationResponse toLocationResponse(Location location) {
    LocationResponse response = new LocationResponse();
    response.setName(location.getName());
    response.setReferenceId(location.getReferenceId());
    return response;
  }

  public static List<String> getTagReferences(List<Tag> tags) {
    List<String> response = new ArrayList<>();
    tags.stream()
        .forEach(
            tag -> {
              response.add(tag.getReferenceId());
            });
    return response;
  }

  public static TagsResponse toTagsResponse(List<Tag> tagEntities) {
    TagsResponse tagsResponse = new TagsResponse();

    List<TagResponse> tags = new ArrayList<>();

    tagEntities.stream()
        .forEach(
            tagEntity -> {
              TagResponse tagResponse = new TagResponse();
              tagResponse.setName(tagEntity.getName());
              tagResponse.setReferenceId(tagEntity.getReferenceId());

              tags.add(tagResponse);
            });

    tagsResponse.setTags(tags);
    return tagsResponse;
  }

  public static SingleProductResponse toSingleProductResponse(
      Product product, List<ProductResponse> similarProducts) {
    SingleProductResponse response = new SingleProductResponse();
    response.setImageUrl(product.getImageUrl());
    response.setLocation(toLocationResponse(product.getLocation()));
    response.setLongDescription(product.getLongDescription());
    response.setName(product.getName());
    response.setReferenceId(product.getReferenceId());
    response.setUrl(product.getUrl());
    response.setVideoEmbedUrl(product.getVideoEmbedUrl());
    response.setAndroidUrl(product.getAndroidAppUrl());
    response.setIosUrl(product.getIosAppUrl());
    response.setUrlSlug(getUrlSlug(product.getName(), product.getReferenceId()));
    response.setShortDescription(product.getShortDescription());
    response.setTags(toTagsResponse(product.getTags()).getTags());
    response.setSimilarProducts(similarProducts);
    response.setPopularity(product.getPopularity());
    response.setBadges(product.getBadges());
    return response;
  }

  private static String getUrlSlug(String productName, String referenceId) {
    // TODO: change the name of the method used below. because we are using it here as well with
    // some other purpose
    return getReferenceIdForTagOrLocation(productName) + "-" + referenceId;
  }

  public static LocationsResponse toLocationsResponse(Iterable<Location> locations) {
    LocationsResponse locationsResponse = new LocationsResponse();
    List<LocationResponse> locationResponses = new ArrayList<>();
    locations.forEach(
        location -> {
          LocationResponse locationResponse = new LocationResponse();
          locationResponse.setReferenceId(location.getReferenceId());
          locationResponse.setName(location.getName());
          locationResponses.add(locationResponse);
        });

    locationsResponse.setLocationResponses(locationResponses);
    return locationsResponse;
  }

  public static File convertMultipartToFile(MultipartFile excelMultipartFile) throws IOException {
    File convFile = new File(excelMultipartFile.getOriginalFilename());
    convFile.createNewFile();
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(excelMultipartFile.getBytes());
    fos.close();
    return convFile;
  }

  public static byte[] convertFileToBytesArray(File file) throws IOException {
    byte[] bytesArray = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    fis.read(bytesArray); // read file into bytes[]
    fis.close();

    return bytesArray;
  }

  public static void writeByteArrayToFile(byte[] bytesArray, String fileLocation, String fileName)
      throws IOException {
    OutputStream os = new FileOutputStream(fileLocation + fileName);
    os.write(bytesArray);
    os.close();
  }
}
