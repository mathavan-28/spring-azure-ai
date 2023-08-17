package com.rai.online.aidemo.controllers;

import com.rai.online.aidemo.apis.model.User;
import com.rai.online.aidemo.apis.model.UserRequest;
import com.rai.online.aidemo.apis.users.SpringAiApi;
import com.rai.online.aidemo.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class UserController implements SpringAiApi {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<User> createUser(@Valid UserRequest userRequest) {
        log.info("creating user - {}", userRequest);
        User userResponse = userService.saveUser(userRequest);
        log.info("User created!..");
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<User> getUserByEmailId(@Valid String userEmail) {
        return new ResponseEntity<>(userService.getUserByEmailId(userEmail), HttpStatus.OK);
    }

//    @Override
//    public ResponseEntity<User> getUserDetailsById(@Valid Long userId) {
//        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
//    }

    @Override
    public ResponseEntity<User> validateUser(@Valid String userEmail, @Valid String password) {
        return new ResponseEntity<>(userService.validateUser(userEmail, password), HttpStatus.OK);
    }
}
