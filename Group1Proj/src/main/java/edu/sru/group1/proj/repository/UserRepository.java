package edu.sru.group1.proj.repository;

import org.springframework.data.repository.CrudRepository;

import edu.sru.group1.proj.domain.StateCounty;

/*
 * Spring Data JPA allows one to implement JPA-based repositories (DAO (Data Access Object) pattern).
 * Spring Data JPA makes it easyt to add CRUD functionality througha powerful later of abstraction 
 * placed on top of a JPA implementation. The abstraction later allows us to access the persistence layer
 * without having to provide our own DAO implementation from scratch.
 * 
 * Extending the CrudRepository interface provides the application woth bsic CRUD functionality on User objects.
 */

public interface UserRepository extends CrudRepository<User, Long> {}