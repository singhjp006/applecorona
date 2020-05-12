package com.corona.apple.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Access;
import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PaginationResponse implements Serializable {
    Long total;
    Long offset;
    Long limit;
}
