package com.corona.apple.dao.repository;

import java.util.List;
import java.util.Optional;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

  Optional<Product> getById(Long id);

  List<Product> getAllByTagsAndLocation(List<Tag> tags, Location location);

  List<Product> getAllByLocation(Location location);

  Page<Product> getAllByIsActiveOrderByPopularity(Boolean isActive, Pageable paginationConfig);

  Page<Product> getAllByLocationInOrderByPopularity(
      List<Location> locations, Pageable paginationConfig);

  Page<Product> getAllByTagsInOrderByPopularity(List<Tag> tags, Pageable paginationConfig);

  Page<Product> getAllByTagsInAndLocationInOrderByPopularity(
      List<Tag> tags, List<Location> locations, Pageable pageable);

  Page<Product> getAllByTagsInAndLocationInAndIdNot( List<Tag> tags, List<Location> locations, Long id,Pageable pageable);

  Optional<Product> getByReferenceId(String referenceId);
}
