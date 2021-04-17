package com.jango.user.service;

import com.jango.user.dto.CreateUserRequest;
import com.jango.user.entity.Role;
import com.jango.user.entity.User;
import com.jango.user.enumeration.Roles;
import com.jango.user.repository.RoleRepository;
import com.jango.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createUser(CreateUserRequest createUserRequest) {
        
        Set<Role> userRoles;
        
        if(createUserRequest.getRoles() == null) {
            userRoles = new HashSet<>();
            userRoles.add(roleRepository.getRoleByName(Roles.ROLE_USER.toString()));
        } else {
            userRoles = createUserRequest.getRoles()
                                         .stream()
                                         .map(name -> roleRepository.getRoleByName(name))
                                         .collect(Collectors.toSet());
        }
        
        User user = User.builder()
                        .name(createUserRequest.getUsername())
                        .description(createUserRequest.getDescription())
                        .email(createUserRequest.getEmail())
                        .password(passwordEncoder.encode(createUserRequest.getPassword()))
                        .creationDate(Timestamp.valueOf(LocalDateTime.now()))
                        .roles(userRoles)
                        .build();

        User savedUser = userRepository.save(user);

        return "Successfully created new user!";
    }
}
