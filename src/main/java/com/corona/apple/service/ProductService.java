package com.corona.apple.service;

import com.corona.apple.dao.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductService {

    @Autowired
    ProductRepository productRepository;
}
