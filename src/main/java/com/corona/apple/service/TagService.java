package com.corona.apple.service;

import com.corona.apple.dao.model.Tag;
import com.corona.apple.dao.repository.TagRepository;
import com.corona.apple.dto.TagsResponse;
import com.corona.apple.dto.request.TagRequest;
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

    public Tag createTag(TagRequest tagRequest) {
        Tag existingTagEntity = tagRepository.getTag(tagRequest.getTagName());
        if (existingTagEntity != null) {
            return existingTagEntity;
        } else {
            Tag tagEntity = MapperHelper.toTag(tagRequest);
            return tagRepository.save(tagEntity);
        }
    }

    public TagsResponse getTags() {
        List<Tag> tagEntities = tagRepository.getAllByIsActive(true);

        return MapperHelper.toTagsResponse(tagEntities);
    }
}
