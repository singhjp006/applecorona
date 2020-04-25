package com.corona.apple.dao.repository;

import java.util.List;
import java.util.Optional;

import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.model.TagClick;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagClickRepository extends CrudRepository<TagClick,Long> {
    Optional<TagClick> getByTagId(Long id);

    List<TagClick> findByTagIn(List<Tag> tags);
}
