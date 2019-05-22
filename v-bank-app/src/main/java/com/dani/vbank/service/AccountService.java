package com.dani.vbank.service;

import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Username;
import com.dani.vbank.spring.SpringContextProvider;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<AccountDetails> getAccountDetailsForUser(Username username);

    AccountDetails getAccountDetails(AccountNumber accountNumber);

    boolean transfer(Transaction transaction);

    List<Transaction> getTransactionHistory(AccountNumber accountNumber);

    static AccountService getInstance() {
        return SpringContextProvider.getApplicationContext().getBean(AccountService.class);
    }
}
