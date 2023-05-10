package com.royalresearch.services.repositories;

import com.royalresearch.services.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM user WHERE username=?1 AND password=sha1(?2)",nativeQuery = true)
    User checkIfUserExists(String username, String password);
}
