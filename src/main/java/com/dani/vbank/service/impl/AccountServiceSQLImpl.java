package com.dani.vbank.service.impl;

import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.service.AccountService;
import com.dani.vbank.model.Transaction;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Log
@Component
public class AccountServiceSQLImpl implements AccountService {

    @Autowired
    private DataSource dataSource;

    @Override
    @SneakyThrows
    public List<AccountDetails> getAccountDetailsForUser(String userName) {
        List<AccountDetails> accountDetailsList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM ACCOUNT ACC WHERE ACC.USERNAME  = '" + userName + "'");
            while (resultSet.next()) {
                AccountDetails accountDetails = new AccountDetails(resultSet.getString("ACCOUNT_ID"),
                        resultSet.getString("USERNAME"),
                        resultSet.getBigDecimal("BALANCE"),
                        resultSet.getString("CURRENCY"));
                accountDetailsList.add(accountDetails);
                log.info("Account found : " + accountDetails);
            }
        }
        return accountDetailsList;
    }

    @Override
    @SneakyThrows
    public AccountDetails getAccountDetails(String accountNo) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT ACCOUNT_ID, USERNAME, BALANCE, CURRENCY " +
                    "FROM ACCOUNT ACC WHERE ACC.ACCOUNT_ID = '" + accountNo + "'");
            if (resultSet.next()) {
                AccountDetails accountDetails = new AccountDetails(resultSet.getString("ACCOUNT_ID"),
                        resultSet.getString("USERNAME"),
                        resultSet.getBigDecimal("BALANCE"),
                        resultSet.getString("CURRENCY"));
                log.info("Account found : " + accountDetails);
                return accountDetails;
            } else {
                log.info("No account found with account No " + accountNo);
                return null;
            }
        }
    }

    @Override
    @SneakyThrows
    public boolean transfer(String fromAcountId, String toAccountId, BigDecimal amount, String currency, String note) {
        currency = currency.toUpperCase();
        try (Connection connection = dataSource.getConnection()) {
            AccountDetails toAccount = getAccountDetails(toAccountId);
            AccountDetails fromAccount = getAccountDetails(fromAcountId);
            //if both account exists we execute the transaction
            if (toAccount != null && fromAccount != null) {
                //update balance of fromAccount
                {
                    BigDecimal newBalance = fromAccount.getBalance().subtract(amount);
                    StringBuilder update = new StringBuilder("UPDATE ACCOUNT SET BALANCE = ").append(newBalance)
                            .append(" WHERE ACCOUNT_ID = '").append(fromAcountId)
                            .append("' AND CURRENCY='").append(currency).append("'");
                    log.info("SQL updating balance (-) : " + update.toString());
                    connection.createStatement().execute(update.toString());
                }
                //update balance of toAccount
                {
                    BigDecimal newBalance = toAccount.getBalance().add(amount);
                    StringBuilder update = new StringBuilder("UPDATE ACCOUNT SET BALANCE = ").append(newBalance)
                            .append(" WHERE ACCOUNT_ID = '").append(toAccountId)
                            .append("' AND CURRENCY='").append(currency).append("'");
                    log.info("SQL updating balance (+) : " + update.toString());
                    connection.createStatement().execute(update.toString());
                }
                {
                    // create transaction record
                    StringBuilder insert = new StringBuilder("INSERT INTO TRANSACTION (FROM_ACCOUNT,TO_ACCOUNT,AMOUNT,CURRENCY,NOTE,EXECUTED) VALUES('")
                            .append(fromAcountId).append("','").append(toAccountId).append("',")
                            .append(amount).append(",'").append(currency).append("','")
                            .append(note).append("',").append(true).append(")");
                    log.info("SQL creating transaction : " + insert.toString());
                    connection.createStatement().execute(insert.toString());
                }
                return true;
            } else {
                // create transaction record
                StringBuilder insert = new StringBuilder("INSERT INTO TRANSACTION (FROM_ACCOUNT,TO_ACCOUNT,AMOUNT,CURRENCY,NOTE,EXECUTED) VALUES('")
                        .append(fromAcountId).append("','").append(toAccountId).append("',")
                        .append(amount).append(",'").append(currency).append("','")
                        .append(note).append("',").append(false).append(")");
                log.info("SQL creating transaction : " + insert.toString());
                connection.createStatement().execute(insert.toString());
                return false;
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Transaction> getTransactionHistory(String accountNo) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM" +
                    " TRANSACTION WHERE FROM_ACCOUNT = '" + accountNo + "' OR TO_ACCOUNT = '" + accountNo + "'");
            List<Transaction> transactions = new ArrayList<>();
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getString("FROM_ACCOUNT"),
                        resultSet.getString("TO_ACCOUNT"),
                        resultSet.getBigDecimal("AMOUNT"),
                        resultSet.getString("CURRENCY"),
                        resultSet.getString("NOTE"),
                        resultSet.getBoolean("EXECUTED")
                );
                transactions.add(transaction);
            }
            return transactions;
        }
    }

}