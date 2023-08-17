package com.rai.online.aidemo.controllers;

import com.rai.online.aidemo.apis.model.Transaction;
import com.rai.online.aidemo.apis.model.TransactionRequest;
import com.rai.online.aidemo.apis.transactions.SpringAiApi;
import com.rai.online.aidemo.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TransactionController implements SpringAiApi {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<Transaction> createTransaction(TransactionRequest transactionRequest) {
        log.info("creating transaction - {}", transactionRequest);
        Transaction transactionResponse = transactionService.saveTransaction(transactionRequest);
        log.info("Transaction created!..");
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Transaction> getTransactionByTransactionId(Long transactionId) {
        return new ResponseEntity<>(transactionService.getTransaction(transactionId), HttpStatus.OK);
    }
}
