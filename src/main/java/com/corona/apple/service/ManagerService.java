package com.corona.apple.service;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.Tag;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.service.mapper.MapperHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManagerService {

    @Autowired
    ProductService productService;

    @Autowired
    LocationService locationService;

    @Autowired
    TagService tagService;

    @Autowired
    HelperClass helperClass;

    public Product createProduct(CreateProductRequest createProductRequest) throws IOException {

        Location location = locationService.getOrCreateLocation(createProductRequest.getLocationName());

        List<Tag> tags = new ArrayList<>();
        createProductRequest.getTags().stream().forEach(tagName -> {
            tags.add(MapperHelper.toTag(tagName));
        });

//        String imageS3Url = helperClass.uploadFileToS3FromUrl(createProductRequest.getImageUrl());
        //hard coding for testing to avoid hitting aws again and again.
        String imageS3Url = "https://apple-corona-product-images.s3.ap-south-1.amazonaws.com/54b17e6c6aebb44f5fc3621af5a11033.png";

        Product product = MapperHelper.toProduct(createProductRequest, imageS3Url, tags,location);

        return productService.createProduct(product);

//        if (location.getProducts() == null) {
//            List<Product> products = new ArrayList<>();
//            products.add(product);
//            location.setProducts(products);
//        } else {
//            location.getProducts().add(product);
//        }

//        return locationService.setLocation(location);
    }
}
