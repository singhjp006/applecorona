package com.corona.apple.service;

import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.repository.TagRepository;
import com.corona.apple.service.mapper.MapperHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagService {

    @Autowired
    TagRepository tagRepository;

    public List<Tag> getTags(List<String> tags) {
        return tagRepository.getTags(tags);
    }

    public Tag createTag(String name) {
        Tag existingTagEntity = tagRepository.getTag(name);
        if (existingTagEntity != null) {
            return existingTagEntity;
        } else {
            Tag tagEntity = MapperHelper.toTag(name);
            return tagRepository.save(tagEntity);
        }
    }
}
