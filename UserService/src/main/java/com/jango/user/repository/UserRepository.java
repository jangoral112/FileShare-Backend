package com.jango.user.repository;

import com.jango.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    public boolean existsByEmail(String email);
    public boolean existsByName(String name);
    public Optional<User> getUserByEmail(String email);
}
