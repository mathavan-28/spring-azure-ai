package com.rai.online.aidemo.controllers;

import com.rai.online.aidemo.apis.accounts.SpringAiApi;
import com.rai.online.aidemo.apis.model.Account;
import com.rai.online.aidemo.apis.model.AccountRequest;
import com.rai.online.aidemo.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class AccountsController implements SpringAiApi {

    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Account> createAccount(@Valid AccountRequest accountRequest) {
        log.info("creating account - {}", accountRequest);
        Account accountResponse = accountService.saveAccount(accountRequest);
        log.info("Account created!..");
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Account> getAccountById(Long accountId) {
        return new ResponseEntity<>(accountService.getAccount(accountId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Account> getAccountByAccountNo(Integer accountNo) {
        return new ResponseEntity<>(accountService.getAccountByAccountNo(accountNo), HttpStatus.OK);
    }
}
