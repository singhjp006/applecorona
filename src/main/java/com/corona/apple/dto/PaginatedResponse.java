package com.corona.apple.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedResponse<T> {

  PaginationResponse pagination;

  List<T> data = new ArrayList<>();

  public static <T> PaginatedResponse<T> from(
      final List<T> responses, final long totalElements, long offset, long limit) {
    final PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>();
    paginatedResponse.setData(responses);
    paginatedResponse.setPagination(new PaginationResponse(totalElements, offset, limit));
    return paginatedResponse;
  }
}
