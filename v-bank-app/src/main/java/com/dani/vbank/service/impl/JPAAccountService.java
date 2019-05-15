package com.dani.vbank.service.impl;

import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import com.dani.vbank.service.AccountService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Log
public class JPAAccountService implements AccountService {

    @Autowired
    private EntityManager entityManager;

    @Override
    @SneakyThrows
    public List<AccountDetails> getAccountDetailsForUser(String userName) {
        Query query = entityManager.createQuery("select ad from AccountDetail ac where ac.ownerId = :uName");
        query.setParameter("uName", userName);
        return query.getResultList();
    }

    @Override
    @SneakyThrows
    public AccountDetails getAccountDetails(String accountNo) {
        return entityManager.find(AccountDetails.class, accountNo);
    }

    @Override
    @SneakyThrows
    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String currency, String note) {
        currency = currency.toUpperCase();
        AccountDetails toAccount = getAccountDetails(toAccountId);
        AccountDetails fromAccount = getAccountDetails(fromAccountId);
        //if both account exists we execute the transaction
        if (toAccount != null && fromAccount != null) {
            //update balance of fromAccount
            {
                BigDecimal newBalance = fromAccount.getBalance().subtract(amount);
                fromAccount.setBalance(newBalance);
            }
            //update balance of toAccount
            {
                BigDecimal newBalance = toAccount.getBalance().add(amount);
                toAccount.setBalance(newBalance);
            }
            // create transaction record
            {
                Transaction transaction = new Transaction();
                transaction.setFromAccountNo(fromAccountId);
                transaction.setToAccountNo(toAccountId);
                transaction.setAmount(amount);
                transaction.setCurrency(currency);
                transaction.setNote(note);
                transaction.setExecuted(true);
                entityManager.persist(transaction);
            }
            //
            entityManager.flush();
            return true;
        } else {
            // create transaction record
//            well, let's support only transactions between local accounts
//            Transaction transaction = new Transaction();
//            transaction.setFromAccountNo(fromAccountId);
//            transaction.setToAccountNo(toAccountId);
//            transaction.setAmount(amount);
//            transaction.setCurrency(currency);
//            transaction.setNote(note);
//            transaction.setExecuted(false);
            return false;
        }
    }

    @Override
    @SneakyThrows
    public List<Transaction> getTransactionHistory(String accountNo) {
        return entityManager.createQuery("select t from Transaction t where t.fromAccountNo = :accountNo or t.toAccountNo")
                .setParameter("accountNo", accountNo).getResultList();
    }
}
