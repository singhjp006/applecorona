package com.corona.apple.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.corona.apple.CommonUtils;
import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.repository.LocationRepository;
import com.corona.apple.dao.repository.ProductRepository;
import com.corona.apple.dao.repository.TagRepository;
import com.corona.apple.dto.PaginatedResponse;
import com.corona.apple.dto.ProductResponse;
import com.corona.apple.dto.SingleProductResponse;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.service.mapper.MapperHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PoroductService {

  ProductRepository productRepository;

  TagRepository tagRepository;

  LocationRepository locationRepository;

  ProductService productService;

  LocationService locationService;

  TagService tagService;

  public Product createProduct(CreateProductRequest createProductRequest) {

    Location location = locationService.getOrCreateLocation(createProductRequest.getLocationName());

    List<Tag> tags = new ArrayList<>();
    createProductRequest
        .getTags()
        .forEach(
            tagName -> {
              tags.add(tagService.createTag(tagName));
            });

    //        String imageS3Url =
    // helperClass.uploadFileToS3FromUrl(createProductRequest.getImageUrl());
    // hard coding for testing to avoid hitting aws again and again.
    String imageS3Url = "https://kinsta.com/wp-content/uploads/2017/04/change-wordpress-url-1.png";

    Product product = MapperHelper.toProduct(createProductRequest, imageS3Url, tags, location);

    return productService.createProduct(product);
  }

  // TODO sorting
  public PaginatedResponse<ProductResponse> getProducts(
      Optional<List<String>> tagReferences,
      Optional<String> locationReference,
      Long offset,
      Long limit) {

    Pageable paginationConfig =
        CommonUtils.getDefaultPaginationObject(
            offset.intValue(), limit.intValue(), "popularity", false);

    List<Tag> tags = new ArrayList<>();
    if (tagReferences.isPresent()) {
      tags = tagRepository.getAllByReferenceIdIn(tagReferences.get());
    }

    List<Location> locations = new ArrayList<>();
    if (locationReference.isPresent()) {
      List<String> locationReferenceIds = new ArrayList<>();
      locationReferenceIds.add("global");

      locationReferenceIds.add(locationReference.get());
      locations = locationRepository.getAllByReferenceIdIn(locationReferenceIds);
    }

    Page<Product> products;
    if ((!(tagReferences.isPresent()) && !(locationReference.isPresent()))) {
      products = productRepository.getAllByIsActiveOrderByPopularity(true, paginationConfig);
    } else if (!tagReferences.isPresent()) {
      products =
          productRepository.getAllByLocationInOrderByPopularity(locations, paginationConfig);
    } else if (!locationReference.isPresent()) {
      products = productRepository.getAllByTagsInOrderByPopularity(tags, paginationConfig);
    } else { // both present
      // passing List<Tag> still saying expected arraylist and you are sending Tag
      products =
          productRepository.getAllByTagsInAndLocationInOrderByPopularity(
              tags, locations, paginationConfig);
    }

    return MapperHelper.toProductResponses(products, limit, offset);
  }

  public SingleProductResponse getProduct(String referenceId, Boolean shouldGetSimilar) {
    Optional<Product> product = productRepository.getByReferenceId(referenceId);

    if (!product.isPresent()) {
      // TODO: change it to Custom exception
      throw new RuntimeException();
    }

    recordViewImpression(referenceId);

    List<ProductResponse> similarProducts = new ArrayList<>();
    if (shouldGetSimilar) {
      similarProducts = getSimilarProducts(product.get(), 3l);
    }

    return MapperHelper.toSingleProductResponse(product.get(), similarProducts);
  }

  public String recordViewImpression(String referenceId) {

    Optional<Product> product = productRepository.getByReferenceId(referenceId);

    if (!product.isPresent()) {
      // TODO: 25/04/20 change it to Custom exception
      throw new RuntimeException();
    }

    try {

      CommonUtils.feedProductView(product.get());

      productRepository.save(product.get());

      product
          .get()
          .getTags()
          .forEach(
              tag -> {
                try {
                  CommonUtils.feedTagView(tag);
                } catch (ParseException e) {
                  e.printStackTrace();
                  throw new RuntimeException(e);
                }
                tagRepository.save(tag);
              });

    } catch (ParseException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception while recording impression");
    }

    return product.get().getUrl();
  }

  public String recordClickImpression(String referenceId) {

    Optional<Product> product = productRepository.getByReferenceId(referenceId);

    if (!product.isPresent()) {
      // TODO: 25/04/20 change it to Custom exception
      throw new RuntimeException();
    }

    try {

      CommonUtils.feedProductAccessCount(product.get());

      productRepository.save(product.get());

      product
          .get()
          .getTags()
          .forEach(
              tag -> {
                try {
                  CommonUtils.feedTagAccessCount(tag);
                } catch (ParseException e) {
                  e.printStackTrace();
                  throw new RuntimeException(e);
                }
                tagRepository.save(tag);
              });

    } catch (ParseException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception while recording impression");
    }

    return product.get().getUrl()+"?ref=https://coronadaily.org/";
  }

  private List<ProductResponse> getSimilarProducts(Product product, Long limit) {

    Pageable pageable =
        CommonUtils.getDefaultPaginationObject(0, limit.intValue(), "popularity", false);

    Page<Product> responses =
        productRepository.getAllByTagsInAndLocationInAndIdNot(
            product.getTags(), Arrays.asList(product.getLocation()), product.getId(), pageable);

    if (responses.getContent().size() < limit) {
      Location global = locationRepository.getByReferenceId("global");

      responses =
          productRepository.getAllByTagsInAndLocationInAndIdNot(
              product.getTags(),
              Arrays.asList(product.getLocation(), global),
              product.getId(),
              pageable);
    }

    return MapperHelper.toProductResponses(responses, limit, 0l).getData();
  }
}
