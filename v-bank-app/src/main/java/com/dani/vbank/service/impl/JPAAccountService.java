package com.dani.vbank.service.impl;

import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Amount;
import com.dani.vbank.model.primitive.Username;
import com.dani.vbank.service.AccountService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Log
@Service
public class JPAAccountService implements AccountService {

    @Autowired
    private EntityManager entityManager;

    @Override
    @SneakyThrows
    public List<AccountDetails> getAccountDetailsForUser(Username userName) {
        Query query = entityManager.createQuery("select ad from AccountDetails ad where ad.username = :uName");
        query.setParameter("uName", userName);
        return query.getResultList();
    }

    @Override
    @SneakyThrows
    public AccountDetails getAccountDetails(AccountNumber accountNo) {
        Query query = entityManager.createQuery("select ad from AccountDetails ad where ad.accountNo = :accountNo");
        query.setParameter("accountNo", accountNo);
        List<AccountDetails> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    @SneakyThrows
    @Transactional
    public boolean transfer(Transaction transaction) {
        if (transaction.isExecuted()) {
            throw new IllegalArgumentException("The transaction has already been executed!");
        }
        AccountDetails toAccount = getAccountDetails(transaction.getToAccountNo());
        AccountDetails fromAccount = getAccountDetails(transaction.getFromAccountNo());
        //if both account exists we execute the transaction
        if (toAccount != null && fromAccount != null) {
            //update balance of fromAccount
            BigDecimal amount = transaction.getAmount().getValue();
            {
                BigDecimal newBalance = fromAccount.getBalance().getValue().subtract(amount);
                fromAccount.setBalance(new Amount(newBalance));
            }
            //update balance of toAccount
            {
                BigDecimal newBalance = toAccount.getBalance().getValue().add(amount);
                toAccount.setBalance(new Amount(newBalance));
            }
            // create transaction record
            {
                transaction.setExecuted(true);
                entityManager.persist(transaction);
            }
            //
            entityManager.flush();
            return true;
        } else {
            // create transaction record
//            well, let's support only transactions between local accounts
            return false;
        }
    }

    @Override
    @SneakyThrows
    public List<Transaction> getTransactionHistory(AccountNumber accountNo) {
        return entityManager.createNamedQuery("transactionsOfAccount")
                .setParameter("accountNo", accountNo).getResultList();
    }
}
