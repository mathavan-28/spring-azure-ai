package com.rai.online.aidemo.repo;

import com.rai.online.aidemo.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TxnRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("select te from TransactionEntity te left outer join AccountEntity ae " + "on te.accountEntity.accountId = ae.accountId where te.accountEntity.accountId = :accountId")
    List<TransactionEntity> findAllByAccountEntity(Long accountId);

    @Query("SELECT case when count(te) > 0 then true else false end FROM TransactionEntity te " + "WHERE te.accountPayee =:loanAccountNo and te.paymentType =:paymentType and te.description =:description")
    boolean existsByAccountPayeeAndPaymentTypeAndDescription(@Param("loanAccountNo") Integer accountPayee, @Param("paymentType") Integer paymentType, @Param("description") String description);
}
