package com.dani.vbank.service;

import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<AccountDetails> getAccountDetailsForUser(String userId);

    AccountDetails getAccountDetails(String accountId);

    boolean transfer(String fromAcountId, String toAccountId, BigDecimal amount, String currency, String note);

    List<Transaction> getTransactionHistory(String accountNo);
}
