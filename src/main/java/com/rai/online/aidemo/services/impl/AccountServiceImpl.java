package com.rai.online.aidemo.services.impl;

import com.rai.online.aidemo.apis.model.Account;
import com.rai.online.aidemo.apis.model.AccountRequest;
import com.rai.online.aidemo.apis.model.Transaction;
import com.rai.online.aidemo.apis.model.TransactionRequest;
import com.rai.online.aidemo.entities.AccountEntity;
import com.rai.online.aidemo.entities.TransactionEntity;
import com.rai.online.aidemo.entities.UserEntity;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import com.rai.online.aidemo.repo.AccountsRepository;
import com.rai.online.aidemo.repo.TxnRepository;
import com.rai.online.aidemo.repo.UserRepository;
import com.rai.online.aidemo.services.AccountService;
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
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountsRepository accountsRepository;

    private final UserRepository userRepository;

    private final TxnRepository txnRepository;

    public AccountServiceImpl(AccountsRepository accountsRepository, UserRepository userRepository, TxnRepository txnRepository) {
        this.accountsRepository = accountsRepository;
        this.userRepository = userRepository;
        this.txnRepository = txnRepository;
    }

    @Transactional
    @Override
    public Account saveAccount(AccountRequest accountRequest) {
        log.info("Account Service.. accounts - {}", accountRequest);
        Account accounts = buildAccountRequest(accountRequest);
        AccountEntity accountsEntity = new AccountEntity();
        convertToEntity(accounts, accountsEntity);
//        validatorService.validateAccount(accounts);
        if (!accountsRepository.existsByAccountNo(accountRequest.getAccountNo())) {
            return convertToModel(accountsRepository.save(accountsEntity));
        } else {
            throw new SpringAIDemoException(E2012, "Account already exists!");
        }
    }

    @Override
    public Account getAccount(Long accountId) {
        AccountEntity accountEntity = accountsRepository.findById(accountId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid Account ID - " + accountId));
        return convertToModel(accountEntity);
    }

    @Override
    public List<Account> getAllAccountsByUserId(Long userId) {
//        List<AccountEntity> accountEntities = accountsRepository.findAllByUserEntity(userId);
        List<Account> accountList = accountsRepository.findAllByUserEntity(userId).stream().map(this::convertToModel).collect(Collectors.toList());
        return accountList;
    }

    @Transactional
    @Override
    public Account updateAccount(Long accountId, Account account) {
        if (nonNull(account.getAccountId()) && !account.getAccountId().equals(accountId)) {
            throw new SpringAIDemoException(E2017, "Account Id mismatch - " + account.getAccountId() + " accountsId - " + accountId);
        }

        if (accountsRepository.existsById(accountId)) {
            Optional<AccountEntity> accountsEntityOptional = accountsRepository.findById(accountId);
            AccountEntity accountsEntity = accountsEntityOptional.orElseThrow();

            trimAccountEntities(account);
            if (!accountsRepository.existsById(account.getAccountId())) {
                return convertToModel(accountsRepository.save(accountsEntity));
            } else {
                throw new SpringAIDemoException(E2012, "Account already exists!");
            }
        } else {
            throw new SpringAIDemoException(E2015, "Account not found with id: " + accountId);
        }
    }

    @Override
    public void deleteAccount(Long accountId) {
        AccountEntity accountsEntity = accountsRepository.findById(accountId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid Account ID - " + accountId));

        accountsRepository.delete(accountsEntity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        List<AccountEntity> accountsEntities = accountsRepository.findAll();
        accountsRepository.deleteAll(accountsEntities);
    }

    public Account getAccountByAccountNo(Integer accountNo) {
        AccountEntity accountEntity = accountsRepository.findByAccountNo(accountNo).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid Account No - " + accountNo));
        return convertToModel(accountEntity);
    }

    private Account buildAccountRequest(AccountRequest accountsRequest) {
        Account accounts = new Account();
        trimAccountEntities(accountsRequest);
        BeanUtils.copyProperties(accountsRequest, accounts);
        return accounts;
    }

    private void trimAccountEntities(AccountRequest accountRequest) {
        accountRequest.setBranchName(accountRequest.getBranchName().trim());
    }

    private Account convertToModel(AccountEntity accountEntity) {
        Account account = new Account();
        BeanUtils.copyProperties(accountEntity, account);
        account.setMonthlyLimit(BigDecimal.valueOf(accountEntity.getMonthlyLimit()));
        account.setUsedLimit(BigDecimal.valueOf(accountEntity.getUsedLimit()));
        account.setCurrentBal(BigDecimal.valueOf(accountEntity.getCurrentBal()));
        account.setUserId(accountEntity.getUserEntity().getUserId());
        List<Transaction> transactionList = fetchAllTransactions(accountEntity.getAccountId());
        if (!ObjectUtils.isEmpty(transactionList)) {
            account.setTransactions(transactionList);
        }
        return account;
    }

    private void convertToEntity(Account account, AccountEntity accountEntity) {
        BeanUtils.copyProperties(account, accountEntity);
        accountEntity.setMonthlyLimit(account.getMonthlyLimit().doubleValue());
        accountEntity.setUsedLimit(account.getUsedLimit().doubleValue());
        accountEntity.setCurrentBal(account.getCurrentBal().doubleValue());
        updateUserEntity(accountEntity, account.getUserId());

        accountEntity.setLastModifiedTime(Timestamp.from(Instant.now()));
    }

    private AccountEntity updateUserEntity(AccountEntity accountEntity, Long userId) {
        if (!ObjectUtils.isEmpty(userId)) {
            UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid User ID - " + userId));
            accountEntity.setUserEntity(userEntity);
        } else {
            accountEntity.setUserEntity(null);
        }
        return accountEntity;
    }

    private List<Transaction> fetchAllTransactions(Long accountId) {
        List<Transaction> transactions = null;
        if (!isNull(accountId)) {
            transactions = txnRepository.findAllByAccountEntity(accountId).stream().map(this::convertToTransactionModel).collect(Collectors.toList());
        }
        return transactions;
    }

    private Transaction convertToTransactionModel(TransactionEntity transactionEntity) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionEntity, transaction);
        transaction.setTransactionId(transactionEntity.getTxnId());
        transaction.setAccountId(transactionEntity.getAccountEntity().getAccountId());
        transaction.setAmount(BigDecimal.valueOf(transactionEntity.getAmount()));
        transaction.setPaymentType(TransactionRequest.PaymentTypeEnum.values()[transactionEntity.getPaymentType()]);
        transaction.setTxnDate(transactionEntity.getTxnDate().toLocalDate());
        return transaction;
    }
}
