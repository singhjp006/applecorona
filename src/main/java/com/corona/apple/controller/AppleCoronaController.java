package com.corona.apple.controller;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.service.HelperClass;
import com.corona.apple.service.ManagerService;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/v1")
public class AppleCoronaController {

    @Autowired
    HelperClass helperClass;

    @Autowired
    ManagerService managerService;

    @ApiOperation(response = String.class, value = "uploadFileToS3")
    @PostMapping(path = "/uploadFile")
    public @ResponseBody String uploadFileToS3(@RequestParam MultipartFile productImage) throws IOException {
        return helperClass.uploadFileToS3(productImage);
    }

    @ApiOperation(response = String.class, value = "uploadFileToS3FromUrl")
    @PostMapping(path = "/uploadFileToS3FromUrl")
    public @ResponseBody String uploadFileToS3FromUrl(@RequestParam String imageUrl) throws IOException {
        return helperClass.uploadFileToS3FromUrl(imageUrl);
    }

    @ApiOperation(response = Location.class, value = "createProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(path = "/createProduct")
    public @ResponseBody Location createProduct(@RequestBody CreateProductRequest createProductRequest) throws IOException {
        return managerService.createProduct(createProductRequest);
    }
}
