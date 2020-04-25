package com.corona.apple.dao.repository;

import java.util.Optional;

import com.corona.apple.dao.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> getById(Long id);
}
