package com.corona.apple.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

  public Boolean importProducts(File file) throws IOException, InvalidFormatException {
    byte[] bytesArray = MapperHelper.convertFileToBytesArray(file);

    Date timestamp = new Date();

    MapperHelper.writeByteArrayToFile(bytesArray, "/tmp/", timestamp.toString());

    File excelFile = new File("/tmp/" + timestamp.toString());

    XSSFWorkbook excelWorkbook = new XSSFWorkbook(excelFile);

    Sheet activeSheet = excelWorkbook.getSheetAt(0);

    int rowCount = (activeSheet.getLastRowNum() - activeSheet.getFirstRowNum()) + 1;

    for (int i = 1; i < rowCount; i++) {
      Row currRow = activeSheet.getRow(i);

      if (!currRow.getCell(6).getStringCellValue().equals("review")){
        continue;
      }

      CreateProductRequest createProductRequest = new CreateProductRequest();
      if (currRow.getCell(0) != null && currRow.getCell(0).getStringCellValue() != null && !currRow.getCell(0).getStringCellValue().isEmpty()) {
        createProductRequest.setName(currRow.getCell(0).getStringCellValue());
      } else {
        System.out.println("No name for this product at row with index: " + i);
        continue;
      }

      if (currRow.getCell(1) != null && currRow.getCell(1).getStringCellValue() != null && !currRow.getCell(1).getStringCellValue().isEmpty()) {
        createProductRequest.setShortDescription(currRow.getCell(1).getStringCellValue());
      } else {
        System.out.println("No short description for: " + currRow.getCell(0).getStringCellValue());
        continue;
      }

      if (currRow.getCell(2) != null && currRow.getCell(2).getStringCellValue() != null && !currRow.getCell(2).getStringCellValue().isEmpty()) {
        List<String> tags = parseCommaSeparatedTags(currRow.getCell(0).getStringCellValue());
        createProductRequest.setTags(tags);
      } else {
        System.out.println("No tags for: " + currRow.getCell(0).getStringCellValue());
        continue;
      }

      if (currRow.getCell(3) != null && currRow.getCell(3).getStringCellValue() != null && !currRow.getCell(3).getStringCellValue().isEmpty()) {
        createProductRequest.setUrl(new URL(currRow.getCell(3).getStringCellValue()));
      } else {
        System.out.println("No URL for: " + currRow.getCell(0).getStringCellValue());
        continue;
      }
      if (currRow.getCell(4) != null && currRow.getCell(4).getStringCellValue() != null && !currRow.getCell(4).getStringCellValue().isEmpty()) {
        createProductRequest.setLongDescription(currRow.getCell(4).getStringCellValue());
      } else {
        System.out.println("No long desc for: " + currRow.getCell(0).getStringCellValue());
        continue;
      }
      if (currRow.getCell(5) != null && currRow.getCell(5).getStringCellValue() != null && !currRow.getCell(5).getStringCellValue().isEmpty()) {
        createProductRequest.setImageUrl(currRow.getCell(5).getStringCellValue());
      } else {
        System.out.println("No img url for: "+ currRow.getCell(0).getStringCellValue());
        continue;
      }

      if (currRow.getCell(8) != null && currRow.getCell(8).getStringCellValue() != null && !currRow.getCell(8).getStringCellValue().isEmpty()) {
        createProductRequest.setVideoUrl(new URL(currRow.getCell(8).getStringCellValue()));
      }
      if (currRow.getCell(9) != null && currRow.getCell(9).getStringCellValue() != null && !currRow.getCell(9).getStringCellValue().isEmpty()) {
        createProductRequest.setAndroidAppUrl(new URL(currRow.getCell(9).getStringCellValue()));
      }
      if (currRow.getCell(10) != null && currRow.getCell(10).getStringCellValue() != null && !currRow.getCell(10).getStringCellValue().isEmpty()) {
        createProductRequest.setIosAppUrl(new URL(currRow.getCell(10).getStringCellValue()));
      }
      if (currRow.getCell(11) != null && currRow.getCell(11).getStringCellValue() != null && !currRow.getCell(11).getStringCellValue().isEmpty()) {
        createProductRequest.setLocationName(currRow.getCell(11).getStringCellValue());
      } else {
        createProductRequest.setLocationName("Global");
      }
      createProductRequest.setIsActive(true);
      createProductRequest.setDevelopedBy("");
      if (createProductRequest != null) {
        Product product = createProduct(createProductRequest);
        System.out.println("Product created with name: " + product.getName());
      }
    }
    return true;
  }

  private List<String> parseCommaSeparatedTags(String string) {
    return Arrays.asList(string.trim().split("\\s*,\\s*"));
  }
}
