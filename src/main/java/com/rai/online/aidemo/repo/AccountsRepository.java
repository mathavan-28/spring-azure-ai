package com.rai.online.aidemo.repo;

import com.rai.online.aidemo.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<AccountEntity, Long> {

    @Query("select ae from AccountEntity ae left outer join UserEntity ue " + "on ae.userEntity.userId = ue.userId where ae.userEntity.userId = :userId")
    List<AccountEntity> findAllByUserEntity(Long userId);

    Optional<AccountEntity> findByAccountNo(@Param("accountNo") Integer accountNo);

    @Query("SELECT case when count(ae) > 0 then true else false end FROM AccountEntity ae " + "WHERE ae.accountNo =:accountNo")
    boolean existsByAccountNo(@Param("accountNo") Integer accountNo);
}
