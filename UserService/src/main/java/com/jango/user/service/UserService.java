package com.jango.user.service;

import com.jango.user.dto.CreateUserRequest;
import com.jango.user.dto.UserDetailsResponse;
import com.jango.user.dto.UserDetailsWithIdResponse;
import com.jango.user.entity.Role;
import com.jango.user.entity.User;
import com.jango.user.enumeration.Roles;
import com.jango.user.exception.UserAlreadyExistException;
import com.jango.user.exception.UserDoesNotExistException;
import com.jango.user.repository.RoleRepository;
import com.jango.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
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

        if(userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new UserAlreadyExistException("User with given username already exist!");
        }
        
        if(userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new UserAlreadyExistException("User with given email already exist!");
        }
        
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
                        .username(createUserRequest.getUsername())
                        .description(createUserRequest.getDescription())
                        .email(createUserRequest.getEmail())
                        .password(passwordEncoder.encode(createUserRequest.getPassword()))
                        .creationDate(Timestamp.valueOf(LocalDateTime.now()))
                        .roles(userRoles)
                        .build();
        
        userRepository.save(user);

        return "Successfully created new user!";
    }

    public UserDetailsResponse getUserDetailsByEmail(String email) {
        Optional<User> optionalUser = userRepository.getUserByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with email: " + email + " does not exist");
        }
        User user = optionalUser.get();

        return UserDetailsResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .description(user.getDescription())
                .creationDate(user.getCreationDate())
                .build();
    }
    
    public UserDetailsWithIdResponse getUserDetailsWithIdByEmail(String email) { // TODO secure
        Optional<User> optionalUser = userRepository.getUserByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with email: " + email + " does not exist");
        }
        User user = optionalUser.get();
        
        return UserDetailsWithIdResponse.builder()
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .description(user.getDescription())
                                        .creationDate(user.getCreationDate())
                                        .id(user.getId())
                                        .build();
    }
    
    public UserDetailsWithIdResponse getUserDetailsWithIdById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with id: " + userId + " does not exist");
        }
        User user = optionalUser.get();
        
        return UserDetailsWithIdResponse.builder()
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .description(user.getDescription())
                                        .creationDate(user.getCreationDate())
                                        .id(user.getId())
                                        .build();
    }
}
