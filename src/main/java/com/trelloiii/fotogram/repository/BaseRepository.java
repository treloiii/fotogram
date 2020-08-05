package com.trelloiii.fotogram.repository;

import com.trelloiii.fotogram.dto.JsonPage;
import com.trelloiii.fotogram.repository.utils.EntityContainer;
import io.r2dbc.spi.*;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.connectionfactory.ConnectionFactoryUtils;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.FetchSpec;
import org.springframework.data.relational.core.mapping.Column;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRepository {
    private final Logger logger = LoggerFactory.getLogger(BaseRepository.class);
    private DatabaseClient client=null;
    @Autowired
    final void setClient(ConnectionFactory connectionFactory){
        client = DatabaseClient.create(connectionFactory);
    }
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
    private DatabaseClient.GenericExecuteSpec getEx(String s,Map<String,Object> binds){
        var ex  = client.execute(s);
        for(var e:binds.entrySet()){
            ex = ex.bind(e.getKey(),e.getValue());
        }
        return ex;
    }


    @Deprecated
    private Mono<Result> getResult(ConnectionFactory connectionFactory, String s) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection ->
                        Mono.from(connection.createStatement(s)
                                .execute())
                                .doFinally(st -> ConnectionFactoryUtils.closeConnection(connection,connectionFactory).then())
                );
    }
    @Deprecated
    private Mono<Result> getResult(ConnectionFactory connectionFactory, String s, Map<String, Object> binds){
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> {
                            connection.beginTransaction();
                            Statement statement = connection.createStatement(s);
                            binds.forEach(statement::bind);
                            Publisher<? extends Result> res = statement.execute();
                            connection.commitTransaction();
                            return Mono.from(res)
                                    .doFinally(st -> ConnectionFactoryUtils.closeConnection(connection,connectionFactory).then());
                        }
                );
    }

    private FetchSpec<Map<String, Object>> fluxResultRows(String s) {
        return client.execute(s).fetch();
    }

    private FetchSpec<Map<String, Object>> fluxResultRows(String s, Map<String, Object> binds) {
        return getEx(s,binds).fetch();
    }

    private Mono<Integer> affectedRows(String s, Map<String, Object> binds) {
        return getEx(s,binds).fetch().rowsUpdated();
    }

    protected Mono<List<Map<String, Object>>> queryMapRows(String s) {
        return fluxResultRows(s)
                .all()
                .collectList();
    }

    protected Mono<Map<String, Object>> queryMapRow(String s) {
        return fluxResultRows(s)
                .first();
    }

    protected Mono<List<Map<String, Object>>> queryMapRows(String s, Map<String, Object> binds) {
        return fluxResultRows(s, binds).all().collectList();
    }

    protected Mono<Map<String, Object>> queryMapRow(String s, Map<String, Object> binds) {
        return fluxResultRows(s, binds).first();
    }

    protected Mono<Integer> queryMap(String s, Map<String, Object> binds) {
        return affectedRows(s, binds);
    }
}
