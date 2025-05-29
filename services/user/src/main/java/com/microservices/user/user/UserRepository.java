package com.microservices.user.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'deletedAt': null }")
    Page<User> findAllCollections(Pageable pageable);

    @Query("{ 'deletedAt': null, 'keycloakId': ?0 }")
    Optional<User> findByKeycloakId(String keycloakId);
}
