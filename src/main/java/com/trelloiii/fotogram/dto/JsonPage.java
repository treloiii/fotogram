package com.trelloiii.fotogram.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.trelloiii.fotogram.views.View;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


public class JsonPage<T> extends PageImpl<T> {
    public JsonPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public JsonPage(List<T> content) {
        super(content);
    }

    @JsonView(View.Important.class)
    @Override
    public int getTotalPages() {
        return super.getTotalPages();
    }

    @JsonView(View.Important.class)
    @Override
    public List<T> getContent() {
        return super.getContent();
    }

    @JsonView(View.Important.class)
    @Override
    public int getNumber() {
        return super.getNumber();
    }
}
