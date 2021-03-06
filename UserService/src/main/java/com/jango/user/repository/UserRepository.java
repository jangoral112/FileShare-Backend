package com.jango.user.repository;

import com.jango.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    public boolean existsByEmail(String email);
    public boolean existsByUsername(String name);
    public Optional<User> getUserByEmail(String email);

    @Query(value = "SELECT * FROM users ORDER BY levenshtein(username, ?1) ASC LIMIT ?2", nativeQuery = true)
    public List<User> findUsersByUsernameByPhrase(String phrase, int limit);
}
