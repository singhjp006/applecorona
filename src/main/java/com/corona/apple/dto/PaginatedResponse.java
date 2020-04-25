package com.corona.apple.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedResponse<T> {

    long totalElements;

    List<T> responses = new ArrayList<>();

    public static <T> PaginatedResponse<T> from(final List<T> responses, final long totalElements) {
        final PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResponses(responses);
        paginatedResponse.setTotalElements(totalElements);
        return paginatedResponse;
    }
}