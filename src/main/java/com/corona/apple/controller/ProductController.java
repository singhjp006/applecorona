package com.corona.apple.controller;

import java.io.IOException;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.service.HelperClass;
import com.corona.apple.service.PoroductService;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("v1/")
public class ProductController {

  @Autowired HelperClass helperClass;

  @Autowired
  PoroductService poroductService;

  @ApiOperation(response = String.class, value = "uploadFileToS3")
  @PostMapping(path = "uploadFile")
  public @ResponseBody String uploadFileToS3(@RequestParam MultipartFile productImage)
      throws IOException {
    return helperClass.uploadFileToS3(productImage);
  }

  @ApiOperation(response = String.class, value = "uploadFileToS3FromUrl")
  @PostMapping(path = "/uploadFileToS3FromUrl")
  public @ResponseBody String uploadFileToS3FromUrl(@RequestParam String imageUrl)
      throws IOException {
    return helperClass.uploadFileToS3FromUrl(imageUrl);
  }

  @ApiOperation(
      response = Location.class,
      value = "createProduct",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping(path = "/products")
  public @ResponseBody Product createProduct(@RequestBody CreateProductRequest createProductRequest)
      throws IOException {
    return poroductService.createProduct(createProductRequest);
  }

  @ApiOperation(
          response = Location.class,
          value = "getProduct",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping(path = "/products/{productId}")
  public @ResponseBody Product getProduct(@RequestBody CreateProductRequest createProductRequest)
          throws IOException {
    return poroductService.createProduct(createProductRequest);
  }

  @ApiOperation(
          response = Location.class,
          value = "Open Product",
          produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping(path = "/products/{productId}/open")
  public @ResponseBody
  RedirectView openProduct(@PathVariable("productId") Long productId) {
    RedirectView redirectView = new RedirectView();
    poroductService.recordImpression(productId);
    redirectView.setUrl("http://www.yahoo.com");
    return redirectView;
  }

}