package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.exceptions.UnreachablePageException;
import com.trelloiii.fotogram.model.Photo;
import io.r2dbc.spi.*;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.mapping.Column;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.*;

public abstract class BaseRepository {

    protected long getTotalRows(String tableName, Pageable pageable, ConnectionFactory connectionFactory) {
        Optional<Long> countOptional = queryMapRows(String.format("select count(*) as count from %s;", tableName), connectionFactory)
                .flatMap(rows -> {
                    long count = (long) rows.get(0).get("count");
                    long totalPages = count / pageable.getPageSize();
                    int pageNumber = pageable.getPageNumber();
                    if (pageNumber > totalPages) {
                        return Mono.just(-1L);
                    }
                    return Mono.just(count);
                })
                .blockOptional();
        long count = countOptional.orElse(-1L);
        if(count==-1)
            throw new UnreachablePageException();
        return count;
    }

    protected <T> List<T> mapObjects(List<Map<String,Object>> rows, Class<T> classToMap){
        List<T> resultList = new ArrayList<>();
        rows.forEach(row-> {
            try {
                T result = (T) classToMap.getDeclaredConstructor().newInstance();
                Field[] fields = classToMap.getDeclaredFields();
                for (Field field : fields) {
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    field.setAccessible(true);
                    if (columnAnnotation != null) {
                        String columnName = columnAnnotation.value();
                        field.set(result, row.get(columnName));
                    } else {
                        String name = field.getName();
                        field.set(result, row.get(name));
                    }
                }
                resultList.add(result);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
        return resultList;
    }

    protected Map<String, Object> rowToMap(Row row, RowMetadata meta) {
        Map<String, Object> rows = new HashMap<>();
        meta.getColumnNames()
                .forEach(name -> {
                    rows.put(name, row.get(name));
                });
        return rows;
    }

    protected Mono<List<Map<String, Object>>> queryMapRows(String s, ConnectionFactory connectionFactory) {

        return Mono.from(connectionFactory.create())
                .flatMap(connection ->
                        Mono.from(connection.createStatement(s)
                                .execute())
                                .doFinally(st -> connection.close())
                )
                .flatMapMany(result -> result.map(this::rowToMap))
                .collectList();
    }

    protected Mono<List<Map<String, Object>>> queryMapRows(String s, ConnectionFactory connectionFactory, Map<String, Object> binds) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> {

                            Statement statement = connection.createStatement(s);
                            binds.forEach(statement::bind);
                            Publisher<? extends Result> res = statement.execute();
                            return Mono.from(res)
                                    .doFinally(st -> connection.close());
                        }
                )
                .flatMapMany(result -> result.map(this::rowToMap))
                .collectList();
    }
}
