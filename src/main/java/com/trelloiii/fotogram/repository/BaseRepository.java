package com.trelloiii.fotogram.repository;

import io.r2dbc.spi.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRepository {
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
