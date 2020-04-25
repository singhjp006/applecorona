package com.corona.apple.dao.repository;

import java.util.Optional;

import com.corona.apple.dao.model.ProductClick;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductClickRepository extends CrudRepository<ProductClick, Long> {

     Optional<ProductClick> getByProductId(Long productId);
}
