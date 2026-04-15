package com.example.demo.dto;

import java.util.List;

public class PagedResponseDto<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PagedResponseDto(List<T> content,
                            int page,
                            int size,
                            long totalElements,
                            int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;

    }

}

