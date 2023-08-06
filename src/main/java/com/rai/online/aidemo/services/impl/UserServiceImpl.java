package com.rai.online.aidemo.services.impl;

import com.rai.online.aidemo.apis.model.User;
import com.rai.online.aidemo.apis.model.UserRequest;
import com.rai.online.aidemo.entities.UserEntity;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import com.rai.online.aidemo.repo.UserRepository;
import com.rai.online.aidemo.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2006;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2012;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2013;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2015;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2017;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User saveUser(UserRequest userRequest) {
        log.info("User Service.. user - {}", userRequest);
        User user = buildUserRequest(userRequest);
        UserEntity userEntity = new UserEntity();
        convertToEntity(user, userEntity);
//        validatorService.validateUser(user);
        if (!userRepository.existsByEmailId(userRequest.getEmail())) {
            return convertToModel(userRepository.save(userEntity));
        } else {
            throw new SpringAIDemoException(E2012, "User already exists!");
        }
    }

    @Override
    public User getUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid User ID - " + userId));
        return convertToModel(userEntity);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User user) {
        if (nonNull(user.getUserId()) && !user.getUserId().equals(userId)) {
            throw new SpringAIDemoException(E2017, "User Id mismatch - " + user.getUserId() + " userId - " + userId);
        }

        if (userRepository.existsById(userId)) {
            Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
            UserEntity userEntity = userEntityOptional.orElseThrow();

            trimUserNames(user);

            if (!userRepository.existsByEmailId(user.getEmail())) {
                return convertToModel(userRepository.save(userEntity));
            } else {
                throw new SpringAIDemoException(E2012, "User already exists!");
            }
        } else {
            throw new SpringAIDemoException(E2015, "User not found with id: " + userId);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid User ID - " + userId));
//        currentBatchService.deleteTransactionsInBatchByDebtorIds(List.of(debtorEntity.getDebtorId()));
        userRepository.delete(userEntity);
    }

    @Override
    public User getUserByEmailId(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpringAIDemoException(E2013, "User not exists with this Mail Id!"));

        return convertToModel(userEntity);
    }

    @Override
    public User validateUser(String userEmail, String password) {
        if (!ObjectUtils.isEmpty(userEmail) && !ObjectUtils.isEmpty(password)) {
            UserEntity userEntity = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new SpringAIDemoException(E2013, "User not exists with this Mail Id!"));

            if (userEntity.getEmail().equals(userEmail) && userEntity.getPassword().equals(password))
                return convertToModel(userEntity);
            else
                return null;
        } else return null;
    }

    @Override
    @Transactional
    public void deleteAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        userRepository.deleteAll(userEntities);
    }

    private User buildUserRequest(UserRequest userRequest) {
        User user = new User();
        trimUserNames(userRequest);
        BeanUtils.copyProperties(userRequest, user);
        return user;
    }

    private void trimUserNames(UserRequest user) {
        user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
    }

    private User convertToModel(UserEntity userEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);

        return user;
    }

    private void convertToEntity(User user, UserEntity userEntity) {
        BeanUtils.copyProperties(user, userEntity);
        userEntity.setLastModifiedTime(Timestamp.from(Instant.now()));
    }
}
