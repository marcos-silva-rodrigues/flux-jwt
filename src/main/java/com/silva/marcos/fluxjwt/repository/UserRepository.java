package com.silva.marcos.fluxjwt.repository;

import com.silva.marcos.fluxjwt.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
