package com.daggerok.oauth2.config.userdetails;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by mak on 4/30/16.
 */
@RepositoryRestResource
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(@Param("username") String username);
}
