package com.jango.user.service;

import com.jango.user.client.AuthServiceClient;
import com.jango.user.client.FileServiceClient;
import com.jango.user.dto.CreateUserRequest;
import com.jango.user.dto.UserDetailsResponse;
import com.jango.user.dto.UserDetailsWithIdResponse;
import com.jango.user.entity.Role;
import com.jango.user.entity.User;
import com.jango.user.enumeration.Roles;
import com.jango.user.exception.UnauthorizedAccessException;
import com.jango.user.exception.UserAlreadyExistException;
import com.jango.user.exception.UserDoesNotExistException;
import com.jango.user.repository.RoleRepository;
import com.jango.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private FileServiceClient fileServiceClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    final private int DEFAULT_USER_SEARCH_LIMIT = 20;

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

    public List<UserDetailsResponse> getUsersDetailsSortedByPhraseFit(String phrase) {
        List<User> users = userRepository.findUsersByUsernameByPhrase(phrase, DEFAULT_USER_SEARCH_LIMIT);

        List<UserDetailsResponse> usersDetailsResponse = new ArrayList<>();

        for(User user: users) {
            UserDetailsResponse userDetailsResponse = UserDetailsResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .description(user.getDescription())
                    .creationDate(user.getCreationDate())
                    .build();

            usersDetailsResponse.add(userDetailsResponse);
        }

        return usersDetailsResponse;
    }

    public List<UserDetailsResponse> getUsersDetails() {

        List<User> users = userRepository.findAll();

        List<UserDetailsResponse> usersDetailsResponse = new ArrayList<>();

        for(User user: users) {
            UserDetailsResponse userDetailsResponse = UserDetailsResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .description(user.getDescription())
                    .creationDate(user.getCreationDate())
                    .build();

            usersDetailsResponse.add(userDetailsResponse);
        }

        return usersDetailsResponse;
    }

    public String deleteUser(String email, String authToken) {

        Boolean tokenOwnerIsAdmin = authServiceClient.parseTokenAuthorities(authToken)
                .stream()
                .anyMatch(roleName -> roleName.equals("ROLE_ADMIN"));

        if(tokenOwnerIsAdmin == false) {
            throw new UnauthorizedAccessException("Only admin can delete user");
        }

        Optional<User> optionalUser = userRepository.getUserByEmail(email);

        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User does not exist");
        }

        fileServiceClient.deleteAllUsersFiles(email, authToken);

        userRepository.delete(optionalUser.get());

        return "Successfully deleted user";
    }
}
