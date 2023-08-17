package com.rai.online.aidemo.controllers;

import com.rai.online.aidemo.apis.loans.SpringAiApi;
import com.rai.online.aidemo.apis.model.LoanAccount;
import com.rai.online.aidemo.apis.model.LoanAccountRequest;
import com.rai.online.aidemo.services.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class LoanController implements SpringAiApi {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Override
    public ResponseEntity<LoanAccount> createLoanAccount(@Valid LoanAccountRequest loanAccountRequest) {
        log.info("creating loan - {}", loanAccountRequest);
        LoanAccount loanResponse = loanService.saveLoan(loanAccountRequest);
        log.info("Loan created!..");
        return new ResponseEntity<>(loanResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<LoanAccount> getLoanAccountByLoanId(Long loanId) {
        return new ResponseEntity<>(loanService.getLoan(loanId), HttpStatus.OK);
    }
}
