package com.trelloiii.fotogram.repository.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class EntityContainer<E> {
    private E entity;
    private Map<String,Object> metadata;
}
