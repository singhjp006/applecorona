package com.corona.apple.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsResponse implements Serializable {

    PaginationResponse pagination;
    List<ProductResponse> products;
}
