package com.corona.apple;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface CommonUtils {

  static Pageable getDefaultPaginationObject(final Integer from, final Integer size) {
    return PageRequest.of(from / size, size, Sort.by("createdAt").descending());
  }
}
