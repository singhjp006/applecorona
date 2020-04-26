package com.corona.apple.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.corona.apple.CommonUtils;
import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.ProductClick;
import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.model.TagClick;
import com.corona.apple.dao.repository.*;
import com.corona.apple.dto.ProductsResponse;
import com.corona.apple.dto.request.CreateProductRequest;
import com.corona.apple.service.mapper.MapperHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PoroductService {

    ProductClickRepository productClickRepository;

    ProductRepository productRepository;

    TagRepository tagRepository;

    LocationRepository locationRepository;

    TagClickRepository tagClickRepository;

    ProductService productService;

    LocationService locationService;

    TagService tagService;

    HelperClass helperClass;

    public Product createProduct(CreateProductRequest createProductRequest) throws IOException {

        Location location = locationService.getOrCreateLocation(createProductRequest.getLocationName());

        List<Tag> tags = new ArrayList<>();
        createProductRequest.getTags()
                .forEach(
                        tagName -> {
                            tags.add(tagService.createTag(tagName));
                        });

        //        String imageS3Url =
        // helperClass.uploadFileToS3FromUrl(createProductRequest.getImageUrl());
        // hard coding for testing to avoid hitting aws again and again.
        String imageS3Url =
                "https://apple-corona-product-images.s3.ap-south-1.amazonaws.com/54b17e6c6aebb44f5fc3621af5a11033.png";

        Product product = MapperHelper.toProduct(createProductRequest, imageS3Url, tags, location);

        return productService.createProduct(product);
    }


    public String recordImpression(Long productId) {

        Optional<Product> product = productRepository.getById(productId);
        if (!product.isPresent()) {
            // TODO: 25/04/20 change it to Custom exception
            throw new RuntimeException();
        }

        // TODO: 25/04/20 implement in corresponding services

        List<TagClick> tagClicks = tagClickRepository.findByTagIn(product.get().getTags());

        tagClicks.forEach(TagClick::increment);

        Optional<ProductClick> productClick = productClickRepository.getByProductId(productId);
        productClick.get().increment();

        // TODO: 25/04/20 Verify bulk update
        tagClickRepository.saveAll(tagClicks);

        productClickRepository.save(productClick.get());

        return product.get().getUrl();

    }

//    public static <T extends AssetResponse> PaginatedResponse<T> getPaginatedResponse(
//            Page<Product> assets) {
//        List<AssetResponse> assetResponses =
//                assets.getContent().stream()
//                        .map(
//                                asset -> {
//                                    AssetResponse assetResponse = getAssetResponse(asset);
//                                    return assetResponse;
//                                })
//                        .collect(Collectors.toList());
//        PaginatedResponse<AssetResponse> paginatedResponse = new PaginatedResponse<>();
//        paginatedResponse.setResponses(assetResponses);
//        paginatedResponse.setTotalElements(assets.getTotalElements());
//        return (PaginatedResponse<T>) paginatedResponse;
//    }

    public ProductsResponse getProducts(Optional<List<String>> tagReferences, Optional<String> locationReference, Long offset, Long limit) {


//        Integer start=10;
//        Integer limit2=50;
//        Pageable paginationConfig =
//                CommonUtils.getDefaultPaginationObject(offset.intValue(), limit.intValue());

//        Page<Product> pages = productRepository.getAllByTags(tags);
        List<Tag> tags = null;
        if (tagReferences.isPresent()) {
            tags = tagRepository.getAllByReferenceIdIn(tagReferences.get());
        }

        List<Location> locations = null;
        if (locationReference.isPresent()) {
            List<String> locationReferenceIds = new ArrayList<>();
            locationReferenceIds.add("global");
            locationReferenceIds.add(locationReference.get());
            locations = locationRepository.getAllByReferenceIdIn(locationReferenceIds);
        }

        List<Product> products;
        if (!(tagReferences.isPresent() && locationReference.isPresent())) {
            products = productRepository.getAllByIsActive(true);
        } else if (!tagReferences.isPresent()) {
            products = productRepository.getAllByLocationIn(locations);
        } else if (!locationReference.isPresent()) {
            products = productRepository.getAllByTagsIn(tags);
        } else { // both present
            products = productRepository.getAllByTagsAndLocations(tags, locations);
        }

        return MapperHelper.toProductsResponse(products);
//        if(tag && location){
//            getAll
//        }
//        else if(tag){
//            getByLocation()
//
//        }
//        else if (location){
//            getAllByTAgs
//        }
//        else{
//            getAllByTAgsAndLocation
//        }




//        List<Product> productRegetAllByTagsAndLocation()
//        if (tagReferences.isPresent()) {
//            List<Tag> tags = tagService.getTags(tagReferences.get());
//        }



        /*

         product                    |                  tag                              |                       product_tag    |        location

         id, name ,...               |      id, name                                    |          pid ,tid                     |

         product p join tag t, join product_tag pt join location l  where p.id=pt.pid and t.id=pt.tid and location.id=p.location_id









        product * tag * product_tags




         */


//        return null;
    }
}
