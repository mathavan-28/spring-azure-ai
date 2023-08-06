package com.rai.online.aidemo.services;

import com.rai.online.aidemo.apis.model.User;
import com.rai.online.aidemo.apis.model.UserRequest;

public interface UserService {

    User saveUser(UserRequest userRequest);

    User getUser(Long userId);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    User getUserByEmailId(String email);

    void deleteAll();

    User validateUser(String userEmail, String password);
}
