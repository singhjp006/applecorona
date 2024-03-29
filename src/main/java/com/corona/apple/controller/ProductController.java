package com.corona.apple.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dto.LocationsResponse;
import com.corona.apple.dto.PaginatedResponse;
import com.corona.apple.dto.ProductResponse;
import com.corona.apple.dto.ProductsResponse;
import com.corona.apple.dto.SingleProductResponse;
import com.corona.apple.dto.TagsResponse;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.dto.request.UpdateProductRequest;
import com.corona.apple.webclient.ClientTest;
import com.corona.apple.webclient.ClientTestCustom;
import com.corona.apple.service.HelperClass;
import com.corona.apple.service.LocationService;
import com.corona.apple.service.PoroductService;
import com.corona.apple.service.TagService;
import com.corona.apple.service.mapper.MapperHelper;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("v1/")
public class ProductController {

  @Autowired HelperClass helperClass;

  @Autowired PoroductService poroductService;

  @Autowired LocationService locationService;

  @Autowired TagService tagService;

  @Autowired ClientTest clientTest;



  @Autowired
  ClientTestCustom clientTestCustom;




  @ApiIgnore
  @ApiOperation(response = String.class, value = "uploadFileToS3")
  @PostMapping(path = "uploadFile")
  public @ResponseBody String uploadFileToS3(@RequestParam MultipartFile productImage)
      throws IOException {
    return helperClass.uploadFileToS3(productImage);
  }


  @ApiIgnore
  @ApiOperation(response = String.class, value = "uploadFileToS3FromUrl")
  @PostMapping(path = "/uploadFileToS3FromUrl")
  public @ResponseBody String uploadFileToS3FromUrl(
      @RequestParam String imageUrl, @RequestHeader("auth") String auth) throws IOException {
    if (Objects.isNull(auth) || !auth.equals("anuragjpaakash"))
      throw new RuntimeException("Invalid request auth");

    return helperClass.uploadFileToS3FromUrl(imageUrl);
  }

  @ApiOperation(
      response = Location.class,
      value = "createProduct",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping(path = "/products")
  public @ResponseBody Product createProduct(
      @RequestBody CreateProductRequest createProductRequest, @RequestHeader("auth") String auth)
      throws IOException {
    if (Objects.isNull(auth) || !auth.equals("anuragjpaakash"))
      throw new RuntimeException("Invalid request auth");
    return poroductService.createProduct(createProductRequest);
  }

  @ApiOperation(
      response = Product.class,
      value = "updateProduct",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PutMapping(path = "/products")
  public @ResponseBody Product updateProduct(
      @RequestBody UpdateProductRequest updateProductRequest, @RequestHeader("auth") String auth) {
    if (Objects.isNull(auth) || !auth.equals("anuragjpaakash"))
      throw new RuntimeException("Invalid request auth");
    return poroductService.updateProduct(updateProductRequest);
  }

  @ApiOperation(response = TagsResponse.class, value = "getTags")
  @GetMapping(path = "/tags")
  public @ResponseBody TagsResponse getTags() {
    return tagService.getTags();
  }

  @ApiOperation(response = LocationsResponse.class, value = "getLocations")
  @GetMapping(path = "/locations")
  public @ResponseBody LocationsResponse getLocations() {
    return locationService.getLocations();
  }

  @ApiOperation(response = ProductsResponse.class, value = "getProducts")
  @GetMapping(path = "/products")
  public @ResponseBody PaginatedResponse<ProductResponse> getProducts(
      @RequestParam(required = false) Optional<List<String>> tagReferences,
      @RequestParam(required = false) Optional<String> locationReference,
      @RequestParam(defaultValue = "0") Long offset,
      @RequestParam(defaultValue = "30") Long limit) {
    return poroductService.getProducts(tagReferences, locationReference, offset, limit);
  }

  @ApiOperation(response = SingleProductResponse.class, value = "getProduct")
  @GetMapping(path = "/products/{referenceId}")
  public @ResponseBody SingleProductResponse getProduct(
      @PathVariable("referenceId") String referenceId,
      @RequestParam(defaultValue = "true") Boolean getSimilar) {
    return poroductService.getProduct(referenceId, getSimilar);
  }

  //  @ApiOperation(
  //      response = Location.class,
  //      value = "getProduct",
  //      produces = MediaType.APPLICATION_JSON_VALUE)
  //  @PostMapping(path = "/products/{productId}")
  //  public @ResponseBody Product getProduct(@RequestBody CreateProductRequest
  // createProductRequest)
  //      throws IOException {
  //    return poroductService.createProduct(createProductRequest);
  //  }

  @ApiOperation(
      response = Location.class,
      value = "Open Product",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping(path = "/products/{referenceId}/open")
  public @ResponseBody RedirectView openProduct(@PathVariable("referenceId") String referenceId) {
    RedirectView redirectView = new RedirectView();
    String url = poroductService.recordClickImpression(referenceId);
    redirectView.setUrl(url);
    return redirectView;
  }

  @ApiOperation(response = Boolean.class, value = "Import Products")
  @PostMapping(path = "/products/import")
  public @ResponseBody Boolean importProducts(
      @RequestParam("file") MultipartFile excelMultipartFile, @RequestHeader("auth") String auth)
      throws IOException, InvalidFormatException {
    if (Objects.isNull(auth) || !auth.equals("anuragjpaakash"))
      throw new RuntimeException("Invalid request auth");

    return poroductService.importProducts(MapperHelper.convertMultipartToFile(excelMultipartFile));
  }
}
