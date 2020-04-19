package com.corona.apple.controller;

import com.corona.apple.service.HelperClass;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/apple-corona")
public class AppleCoronaController {

    @Autowired
    HelperClass helperClass;

    @ApiOperation(response = String.class, value = "uploadFileToS3")
    @PostMapping(path = "/uploadFile")
    public @ResponseBody String uploadFileToS3(@RequestParam MultipartFile productImage) throws IOException {
        return helperClass.uploadFileToS3(productImage);
    }

    @ApiOperation(response = String.class, value = "uploadFileToS3FromUrl")
    @PostMapping(path = "uploadFileToS3FromUrl")
    public @ResponseBody String uploadFileToS3FromUrl(@RequestParam String imageUrl) throws IOException {
        return helperClass.uploadFileToS3FromUrl(imageUrl);
    }

}
