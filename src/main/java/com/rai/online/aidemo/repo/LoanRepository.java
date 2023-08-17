package com.rai.online.aidemo.repo;

import com.rai.online.aidemo.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    @Query("select le from LoanEntity le left outer join UserEntity ue " + "on le.userEntity.userId = ue.userId where le.userEntity.userId = :userId")
    List<LoanEntity> findAllByUserEntity(Long userId);

    @Query("SELECT case when count(le) > 0 then true else false end FROM LoanEntity le " + "WHERE le.loanAccountNo =:loanAccountNo")
    boolean existsByLoanAccountNo(@Param("loanAccountNo") Integer loanAccountNo);
}
