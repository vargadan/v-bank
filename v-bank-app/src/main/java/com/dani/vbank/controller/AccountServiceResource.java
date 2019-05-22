package com.dani.vbank.controller;

import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Amount;
import com.dani.vbank.model.primitive.Currency;
import com.dani.vbank.service.AccountService;
import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountServiceResource {

    @Autowired
    private AccountService delegate;

    @Autowired
    private DataGenerator generator;

    @RequestMapping(path = "/api/v1/account/{accountId}", produces = "application/json")
    public AccountDetails getAccountDetails(@PathVariable("accountId") String accountId) {
        AccountDetails details = delegate.getAccountDetails(new AccountNumber(accountId));
        return details;
    }

    @RequestMapping(path = "/api/v1/account/{accountId}/transactions")
    @ResponseBody
    public List<Transaction> getTransactionHistory(@PathVariable String accountId) {
        return delegate.getTransactionHistory(new AccountNumber(accountId));
    }

    @RequestMapping(path = "/api/v1/transfer", method = {RequestMethod.GET, RequestMethod.POST})
    public void transfer(@RequestParam String fromAcountId, @RequestParam String toAccountId,
                         @RequestParam BigDecimal amount, @RequestParam(defaultValue = "CHF") Currency currency,
                         @RequestParam String note) {
        Transaction transaction = new Transaction(null, new AccountNumber(fromAcountId), new AccountNumber(toAccountId), new Amount(amount), currency, note, false);
        delegate.transfer(transaction);
    }
}