package com.rai.online.aidemo.services.impl;

import com.rai.online.aidemo.apis.model.LoanAccount;
import com.rai.online.aidemo.apis.model.LoanAccountRequest;
import com.rai.online.aidemo.entities.LoanEntity;
import com.rai.online.aidemo.entities.UserEntity;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import com.rai.online.aidemo.repo.LoanRepository;
import com.rai.online.aidemo.repo.UserRepository;
import com.rai.online.aidemo.services.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2006;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2012;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2015;
import static com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode.E2017;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    private final UserRepository userRepository;

    public LoanServiceImpl(LoanRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public LoanAccount saveLoan(LoanAccountRequest loanAccountRequest) {
        log.info("LoanAccount Service.. loanAccounts - {}", loanAccountRequest);
        LoanAccount loanAccounts = buildLoanRequest(loanAccountRequest);
        LoanEntity loanAccountsEntity = new LoanEntity();
        convertToEntity(loanAccounts, loanAccountsEntity);
//        validatorService.validateLoanAccount(loanAccounts);
        if (!loanRepository.existsByLoanAccountNo(loanAccountRequest.getLoanAccountNo())) {
            return convertToModel(loanRepository.save(loanAccountsEntity));
        } else {
            throw new SpringAIDemoException(E2012, "Loan Account already exists!");
        }
    }

    @Override
    public LoanAccount getLoan(Long loanId) {
        LoanEntity loanAccountEntity = loanRepository.findById(loanId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid LoanAccount ID - " + loanId));
        return convertToModel(loanAccountEntity);
    }

    @Override
    public List<LoanAccount> getAllLoanAccountsByUserId(Long userId) {
//        List<LoanEntity> loanEntities = loanRepository.findAllByUserEntity(userId);
        List<LoanAccount> loanAccountList = loanRepository.findAllByUserEntity(userId).stream().map(this::convertToModel).collect(Collectors.toList());
        return loanAccountList;
    }

    @Transactional
    @Override
    public LoanAccount updateLoan(Long loanId, LoanAccount loanAccount) {
        if (nonNull(loanAccount.getLoanId()) && !loanAccount.getLoanId().equals(loanId)) {
            throw new SpringAIDemoException(E2017, "LoanAccount Id mismatch - " + loanAccount.getLoanId() + " loanAccountsId - " + loanId);
        }

        if (loanRepository.existsById(loanId)) {
            Optional<LoanEntity> loanAccountsEntityOptional = loanRepository.findById(loanId);
            LoanEntity loanAccountsEntity = loanAccountsEntityOptional.orElseThrow();

            trimLoanAccountEntities(loanAccount);
            if (!loanRepository.existsById(loanAccount.getLoanId())) {
                return convertToModel(loanRepository.save(loanAccountsEntity));
            } else {
                throw new SpringAIDemoException(E2012, "LoanAccount already exists!");
            }
        } else {
            throw new SpringAIDemoException(E2015, "LoanAccount not found with id: " + loanId);
        }
    }

    @Transactional
    @Override
    public void deleteLoan(Long loanId) {
        LoanEntity loanAccountsEntity = loanRepository.findById(loanId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid LoanAccount ID - " + loanId));

        loanRepository.delete(loanAccountsEntity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        List<LoanEntity> loanAccountEntities = loanRepository.findAll();
        loanRepository.deleteAll(loanAccountEntities);
    }

    private LoanAccount buildLoanRequest(LoanAccountRequest loanAccountRequest) {
        LoanAccount loanAccounts = new LoanAccount();
//        trimLoanAccountNames(loanAccountsRequest);
        BeanUtils.copyProperties(loanAccountRequest, loanAccounts);
        return loanAccounts;
    }

    private void trimLoanAccountEntities(LoanAccountRequest loanAccountRequest) {
        loanAccountRequest.setPlanName(loanAccountRequest.getPlanName().trim());
    }

    private LoanAccount convertToModel(LoanEntity loanAccountEntity) {
        LoanAccount loanAccount = new LoanAccount();
        BeanUtils.copyProperties(loanAccountEntity, loanAccount);
        loanAccount.setMinSanctionedAmount(BigDecimal.valueOf(loanAccountEntity.getMinSanctionedAmount()));
        loanAccount.setInterestRate(BigDecimal.valueOf(loanAccountEntity.getInterestRate()));
        loanAccount.setUserId(loanAccountEntity.getUserEntity().getUserId());
        return loanAccount;
    }

    private void convertToEntity(LoanAccount loanAccount, LoanEntity loanAccountEntity) {
        BeanUtils.copyProperties(loanAccount, loanAccountEntity);
        loanAccountEntity.setMinSanctionedAmount(loanAccount.getMinSanctionedAmount().doubleValue());
        loanAccountEntity.setInterestRate(loanAccount.getInterestRate().doubleValue());
        loanAccountEntity.setMaxTenure(Integer.parseInt(loanAccount.getMaxTenure().toString()));
        updateUserEntity(loanAccountEntity, loanAccount.getUserId());

        loanAccountEntity.setLastModifiedTime(Timestamp.from(Instant.now()));
    }

    private LoanEntity updateUserEntity(LoanEntity loanAccountEntity, Long userId) {
        if (!ObjectUtils.isEmpty(userId)) {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid User ID - " + userId));
            loanAccountEntity.setUserEntity(userEntity);
        } else {
            loanAccountEntity.setUserEntity(null);
        }
        return loanAccountEntity;
    }
}
