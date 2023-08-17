package com.rai.online.aidemo.services;

import com.rai.online.aidemo.apis.model.Transaction;
import com.rai.online.aidemo.apis.model.TransactionRequest;

import java.util.List;

public interface TransactionService {

    Transaction saveTransaction(TransactionRequest transactionRequest);

    Transaction getTransaction(Long transactionId);

    Transaction updateTransaction(Long transactionId, Transaction transaction);

    void deleteTransaction(Long transactionId);

    List<Transaction> getAllTransactionsByAccountId(Long accountId);

    void deleteAll();
}
