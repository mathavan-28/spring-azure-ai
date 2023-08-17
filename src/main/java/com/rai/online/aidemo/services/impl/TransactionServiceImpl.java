package com.rai.online.aidemo.services.impl;

import com.rai.online.aidemo.apis.model.Transaction;
import com.rai.online.aidemo.apis.model.TransactionRequest;
import com.rai.online.aidemo.entities.AccountEntity;
import com.rai.online.aidemo.entities.TransactionEntity;
import com.rai.online.aidemo.exceptions.SpringAIDemoException;
import com.rai.online.aidemo.repo.AccountsRepository;
import com.rai.online.aidemo.repo.TxnRepository;
import com.rai.online.aidemo.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
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
public class TransactionServiceImpl implements TransactionService {

    private final TxnRepository txnRepository;

    private final AccountsRepository accountsRepository;

    public TransactionServiceImpl(TxnRepository txnRepository, AccountsRepository accountsRepository) {
        this.txnRepository = txnRepository;
        this.accountsRepository = accountsRepository;
    }

    @Transactional
    @Override
    public Transaction saveTransaction(TransactionRequest transactionRequest) {
        log.info("Transaction Service.. transactions - {}", transactionRequest);
        Transaction transactions = buildTransactionRequest(transactionRequest);
        TransactionEntity transactionsEntity = new TransactionEntity();
        convertToEntity(transactions, transactionsEntity);
//        validatorService.validateTransaction(transactions);
        if (!txnRepository.existsByAccountPayeeAndPaymentTypeAndDescription(transactionRequest.getAccountPayee(), transactionRequest.getPaymentType().ordinal(), transactionRequest.getDescription())) {
            return convertToModel(txnRepository.save(transactionsEntity));
        } else {
            throw new SpringAIDemoException(E2012, "Transaction already exists!");
        }
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        TransactionEntity transactionEntity = txnRepository.findById(transactionId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid Transaction ID - " + transactionId));
        return convertToModel(transactionEntity);
    }

    @Override
    public List<Transaction> getAllTransactionsByAccountId(Long accountId) {
        List<TransactionEntity> transactionEntities = txnRepository.findAllByAccountEntity(accountId);
        List<Transaction> transactionList = txnRepository.findAllByAccountEntity(accountId).stream().map(this::convertToModel).collect(Collectors.toList());
        return transactionList;
    }

    @Transactional
    @Override
    public Transaction updateTransaction(Long transactionId, Transaction transaction) {
        if (nonNull(transaction.getTransactionId()) && !transaction.getTransactionId().equals(transactionId)) {
            throw new SpringAIDemoException(E2017, "Transaction Id mismatch - " + transaction.getTransactionId() + " transactionsId - " + transactionId);
        }

        if (txnRepository.existsById(transactionId)) {
            Optional<TransactionEntity> transactionsEntityOptional = txnRepository.findById(transactionId);
            TransactionEntity transactionsEntity = transactionsEntityOptional.orElseThrow();

            trimTransactionEntities(transaction);
            if (!txnRepository.existsById(transaction.getTransactionId())) {
                return convertToModel(txnRepository.save(transactionsEntity));
            } else {
                throw new SpringAIDemoException(E2012, "Transaction already exists!");
            }
        } else {
            throw new SpringAIDemoException(E2015, "Transaction not found with id: " + transactionId);
        }
    }

    @Transactional
    @Override
    public void deleteTransaction(Long transactionId) {
        TransactionEntity transactionsEntity = txnRepository.findById(transactionId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid Transaction ID - " + transactionId));

        txnRepository.delete(transactionsEntity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        List<TransactionEntity> transactionEntities = txnRepository.findAll();
        txnRepository.deleteAll(transactionEntities);
    }

    private Transaction buildTransactionRequest(TransactionRequest transactionsRequest) {
        Transaction transactions = new Transaction();
//        trimTransactionNames(transactionsRequest);
        BeanUtils.copyProperties(transactionsRequest, transactions);
        return transactions;
    }

    private void trimTransactionEntities(TransactionRequest transactionRequest) {
        transactionRequest.setDescription(transactionRequest.getDescription().trim());
    }

    private Transaction convertToModel(TransactionEntity transactionEntity) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionEntity, transaction);
        transaction.setTransactionId(transactionEntity.getTxnId());
        transaction.setAccountId(transactionEntity.getAccountEntity().getAccountId());
        transaction.setAmount(BigDecimal.valueOf(transactionEntity.getAmount()));
        transaction.setPaymentType(TransactionRequest.PaymentTypeEnum.values()[transactionEntity.getPaymentType()]);
        transaction.setTxnDate(transactionEntity.getTxnDate().toLocalDate());
        return transaction;
    }

    private void convertToEntity(Transaction transaction, TransactionEntity transactionEntity) {
        BeanUtils.copyProperties(transaction, transactionEntity);
        transactionEntity.setTxnDate(Date.valueOf(transaction.getTxnDate()));
        transactionEntity.setAmount(transaction.getAmount().doubleValue());
        transactionEntity.setPaymentType(TransactionRequest.PaymentTypeEnum.fromValue(transaction.getPaymentType().toString()).ordinal());
        updateAccountEntity(transactionEntity, transaction.getAccountId());

        transactionEntity.setLastModifiedTime(Timestamp.from(Instant.now()));
    }

    private TransactionEntity updateAccountEntity(TransactionEntity transactionEntity, Long accountId) {
        if (!ObjectUtils.isEmpty(accountId)) {
            AccountEntity accountEntity = accountsRepository.findById(accountId).orElseThrow(() -> new SpringAIDemoException(E2006, "Invalid Account ID - " + accountId));
            transactionEntity.setAccountEntity(accountEntity);
        } else {
            transactionEntity.setAccountEntity(null);
        }
        return transactionEntity;
    }
}
