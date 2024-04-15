package com.dimas.persistence;

import com.dimas.domain.FriendRequestCreate;
import com.dimas.domain.entity.FriendRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.dimas.util.Const.SCHEMA_NAME;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class FriendRequestRepository {

    private final PgPool pgPool;

    public Uni<Void> create(FriendRequestCreate request) {
        log.info("persisting FriendRequestCreate={}", request);
        return pgPool.withTransaction(conn -> conn.preparedQuery("""
                        INSERT INTO %s.friend_request (user_id, friend_id, created_at)
                                    VALUES ($1, $2, $3)
                        """.formatted(SCHEMA_NAME))
                .execute(map(request))
                .replaceWithVoid()
        );
    }

    private FriendRequest map(Row row) {
        return FriendRequest.builder()
                .userId(row.getUUID("user_id"))
                .friendId(row.getUUID("friend_id"))
                .build();
    }

    private Tuple map(FriendRequestCreate request) {
        return Tuple.tuple()
                .addUUID(request.getUserId())
                .addUUID(request.getFriendId())
                .addLocalDateTime(LocalDateTime.now());
    }

}
