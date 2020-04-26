package com.corona.apple.dao.repository;

import com.corona.apple.dao.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE name IN (:tags) AND isActive=1")
    List<Tag> getTags(@Param("tags") List<String> tags);

    @Query("SELECT t FROM Tag t WHERE name=:name AND isActive=1")
    Tag getTag(@Param("name") String name);

//    @Query("SELECT t FROM Tag t WHERE referenceId IN (:referenceIds) AND isActive=1")
    List<Tag> getAllByReferenceIdIn(@Param("referenceIds") List<String> referenceIds);

    List<Tag> getAllByIsActive(Boolean isActive);
}
