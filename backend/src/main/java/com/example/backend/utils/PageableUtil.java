package com.example.backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    public static Pageable validPaginationAndGetPageable(String sortType, int page, int pageSize, String sortField)
            throws IllegalArgumentException {

        if (sortType == null) {
            throw new IllegalArgumentException("Sort type cannot be null");
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }

        if (pageSize < 0) {
            throw new IllegalArgumentException("Page size must be non-negative");
        }

        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortField);
            return PageRequest.of(page, pageSize, sort);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sortType value! Must be 'DESC' or 'ASC");
        }
    }
}
