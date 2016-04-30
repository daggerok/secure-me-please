package com.daggerok.oauth2.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by mak on 4/30/16.
 */
@RepositoryRestResource
public interface DomainRepository extends MongoRepository<Domain, String> {}
