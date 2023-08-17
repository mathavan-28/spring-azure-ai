package com.rai.online.aidemo.services;

import com.rai.online.aidemo.apis.model.LoanAccount;
import com.rai.online.aidemo.apis.model.LoanAccountRequest;

import java.util.List;

public interface LoanService {

    LoanAccount saveLoan(LoanAccountRequest loanAccountRequest);

    LoanAccount getLoan(Long loanId);

    LoanAccount updateLoan(Long loanId, LoanAccount loanAccount);

    void deleteLoan(Long loanId);

    void deleteAll();

    List<LoanAccount> getAllLoanAccountsByUserId(Long userId);
}
