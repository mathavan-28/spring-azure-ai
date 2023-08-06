package com.rai.online.aidemo.repo;

import com.rai.online.aidemo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT case when count(ue) > 0 then true else false end FROM UserEntity ue " +
            "WHERE ue.email =:emailId")
    boolean existsByEmailId(@Param("emailId") String emailId);

    Optional<UserEntity> findByEmail(@Param("emailId") String emailId);

    void deleteAllByUserId(Integer userId);
}
