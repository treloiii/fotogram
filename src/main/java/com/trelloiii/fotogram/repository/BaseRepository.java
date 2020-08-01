package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.dto.JsonPage;
import com.trelloiii.fotogram.exceptions.UnreachablePageException;
import com.trelloiii.fotogram.model.Photo;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import io.r2dbc.spi.*;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.mapping.Column;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class BaseRepository {

    protected <T> Mono<Page<T>> pagedEntity(EntityContainer<List<T>> ec, Pageable pageable) {
        Map<String, Object> meta = ec.getMetadata();
        if (meta.size() < 1)
            return Mono.just(new JsonPage<>(ec.getEntity()));
        return Mono.just(new JsonPage<>(ec.getEntity(), pageable, (long) meta.get("count")));
    }

    private <T> EntityContainer<T> extractObjectFromRow(Map<String, Object> row, Class<T> classToMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T result = classToMap.getDeclaredConstructor().newInstance();
        Field[] fields = classToMap.getDeclaredFields();
        for (Field field : fields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            field.setAccessible(true);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.value();
                field.set(result, row.remove(columnName));
            } else {
                String name = field.getName();
                field.set(result, row.remove(name));
            }
        }
        return new EntityContainer<>(result, row);
    }

    protected <T> EntityContainer<List<T>> mapObjects(List<Map<String, Object>> rows, Class<T> classToMap) {
        List<T> resultList = new ArrayList<>();
        Map<String, Object> totalMeta = new HashMap<>();
        rows.forEach(row -> {
            try {
                EntityContainer<T> entityContainer = extractObjectFromRow(row, classToMap);
                if (entityContainer.getMetadata() != null)
                    totalMeta.putAll(entityContainer.getMetadata());
                resultList.add(entityContainer.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return new EntityContainer<>(resultList, totalMeta);
    }

    @SneakyThrows
    protected <T> EntityContainer<T> mapObject(Map<String, Object> row, Class<T> classToMap) {
        return extractObjectFromRow(row, classToMap);
    }

    protected Map<String, Object> rowToMap(Row row, RowMetadata meta) {
        Map<String, Object> rows = new HashMap<>();
        System.out.println(rows);
        meta.getColumnNames()
                .forEach(name -> rows.put(name, row.get(name)));
        return rows;
    }


    private Mono<Result> getResult(ConnectionFactory connectionFactory, String s) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection ->
                        Mono.from(connection.createStatement(s)
                                .execute())
                                .doFinally(st -> connection.close())
                );
    }
    private Mono<Result> getResult(ConnectionFactory connectionFactory, String s, Map<String, Object> binds){
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> {
                            Statement statement = connection.createStatement(s);
                            binds.forEach(statement::bind);
                            Publisher<? extends Result> res = statement.execute();
                            return Mono.from(res)
                                    .doFinally(st -> connection.close());
                        }
                );
    }

    private Flux<Map<String, Object>> fluxResultRows(ConnectionFactory connectionFactory, String s) {
        return getResult(connectionFactory, s)
                .flatMapMany(result -> result.map(this::rowToMap));
    }

    private Flux<Map<String, Object>> fluxResultRows(ConnectionFactory connectionFactory, String s, Map<String, Object> binds) {
        return getResult(connectionFactory, s, binds)
                .flatMapMany(result -> result.map(this::rowToMap));
    }

    private Mono<Integer> affectedRows(ConnectionFactory connectionFactory, String s, Map<String, Object> binds) {
        return getResult(connectionFactory, s, binds)
                .flatMap(result -> Mono.from(result.getRowsUpdated()));
    }

    protected Mono<List<Map<String, Object>>> queryMapRows(String s, ConnectionFactory connectionFactory) {
        return fluxResultRows(connectionFactory, s)
                .collectList();
    }

    protected Mono<Map<String, Object>> queryMapRow(String s, ConnectionFactory connectionFactory) {
        return fluxResultRows(connectionFactory, s)
                .take(1)
                .collectList()
                .flatMap(list -> Mono.just(list.get(0)));
    }

    protected Mono<List<Map<String, Object>>> queryMapRows(String s, ConnectionFactory connectionFactory, Map<String, Object> binds) {
        return fluxResultRows(connectionFactory, s, binds)
                .collectList();
    }

    protected Mono<Map<String, Object>> queryMapRow(String s, ConnectionFactory connectionFactory, Map<String, Object> binds) {
        return fluxResultRows(connectionFactory, s, binds)
                .take(1)
                .collectList()
                .flatMap(list -> Mono.just(list.get(0)));
    }

    protected Mono<Integer> queryMap(String s, ConnectionFactory connectionFactory, Map<String, Object> binds) {
        return affectedRows(connectionFactory, s, binds);
    }
}
