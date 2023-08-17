package com.rai.online.aidemo.services;

import com.rai.online.aidemo.apis.model.Account;
import com.rai.online.aidemo.apis.model.AccountRequest;

import java.util.List;

public interface AccountService {

    Account saveAccount(AccountRequest accountRequest);

    Account getAccount(Long accountId);

    Account updateAccount(Long accountId, Account account);

    void deleteAccount(Long accountId);

    void deleteAll();

    Account getAccountByAccountNo(Integer accountNo);

    List<Account> getAllAccountsByUserId(Long userId);
}
