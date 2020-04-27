package com.corona.apple.dao.repository;

import java.util.List;
import java.util.Optional;

import com.corona.apple.dao.model.Location;
import com.corona.apple.dao.model.Product;
import com.corona.apple.dao.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

  Optional<Product> getById(Long id);

  List<Product> getAllByTagsAndLocation(List<Tag> tags, Location location);

  List<Product> getAllByLocation(Location location);

  Page<Product> getAllByIsActive(Boolean isActive, Pageable paginationConfig);

  Page<Product> getAllByLocationIn(List<Location> locations, Pageable paginationConfig);

  Page<Product> getAllByTagsIn(List<Tag> tags, Pageable paginationConfig);

//  List<Product> getAllByTagInAndLocationIn(List<Tag> tags, List<Location> locations);

  @Query("SELECT p FROM Product p WHERE tags IN (:tags) AND location IN (:locations)")
  Page<Product> getAllByTagsAndLocations(@Param("tags") List<Tag> tags, @Param("locations") List<Location> locations, Pageable paginationConfig);

  Optional<Product> getByReferenceId(String referenceId);


//  Page<Product> getAllByTags(List<Tag> tags/*, Pageable pageable*/);



//  @Query(
//          "select p from Product p join p.tags t where t.id in :tagIds"
//      )
//  Page<Product> findByAdSet(@Param("tagIds") List<String> tagIds,Pageable pageable);

}
