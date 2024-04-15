package com.dimas.service;

import com.dimas.api.model.ApiPost;
import com.dimas.domain.PostCreate;
import com.dimas.domain.PostUpdate;
import com.dimas.domain.entity.Person;
import com.dimas.domain.entity.Post;
import com.dimas.domain.mapper.PostMapper;
import com.dimas.persistence.PersonRepository;
import com.dimas.persistence.PostRepository;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PostService {

    private final AuthenticationContext context;
    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    private final PostMapper postMapper;

    @Inject
    @CacheName("feed-cache")
    Cache cache;

    public Uni<Post> create(PostCreate request) {
        log.debug("currentUser={}", context.getCurrentUser());
        var userId = context.getCurrentUser().getId();
        return personRepository.findFriends(userId)
                .onItem().transformToUni(this::invalidateList)
                .onItem().transformToUni(v -> {
                    var post = postMapper.map(request).withFromUser(userId);
                    return postRepository.create(post);
                });
    }

    private Uni<Void> invalidateList(List<Person> ids) {
        log.debug("Invalidating {} friends feeds", ids.size());
        return Multi.createFrom().iterable(ids)
                .onItem().transformToUni(person -> cache.invalidate(person.getId().toString()))
                .concatenate().collect().asList()
                .replaceWithVoid();
    }

    public Uni<Void> update(PostUpdate request) {
        return postRepository.update(request);
    }

    public Uni<Post> getById(UUID id) {
        return postRepository.getById(id);
    }

    public Uni<Void> delete(UUID id) {
        return postRepository.delete(id)
                .replaceWithVoid();
    }

    public Uni<List<ApiPost>> feed(Long offset, Long limit) {
        log.debug("currentUser={}", context.getCurrentUser());
        var userId = context.getCurrentUser().getId();
        log.debug("GET FEED");
        return cache.getAsync(userId.toString(), f -> {
                    log.debug("CACHE MISS");
                    return postRepository.feed(userId, offset, limit)
                            .map(list -> list.stream().map(postMapper::map).toList());
                })
                .invoke(l -> log.debug("FEED END"));

    }
}
