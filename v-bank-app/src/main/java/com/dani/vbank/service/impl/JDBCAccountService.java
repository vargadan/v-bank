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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log
//@Component
public class JDBCAccountService implements AccountService {

    @Autowired
    private DataSource dataSource;

    @Override
    @SneakyThrows
    public List<AccountDetails> getAccountDetailsForUser(String userName) {
        List<AccountDetails> accountDetailsList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ACCOUNT ACC WHERE ACC.USERNAME  = ?");
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ACCOUNT_ID, USERNAME, BALANCE, CURRENCY " +
                    "FROM ACCOUNT ACC WHERE ACC.ACCOUNT_ID = ?");
            preparedStatement.setString(1, accountNo);
            ResultSet resultSet = preparedStatement.executeQuery();
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
    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String currency, String note) {
        currency = currency.toUpperCase();

            AccountDetails toAccount = getAccountDetails(toAccountId);
            AccountDetails fromAccount = getAccountDetails(fromAccountId);
            //if both account exists we execute the transaction
            if (toAccount != null && fromAccount != null) {
                //update balance of fromAccount
                {
                    BigDecimal newBalance = fromAccount.getBalance().subtract(amount);
                    updateAccountBalance(fromAccountId, newBalance, currency);
                }
                //update balance of toAccount
                {
                    BigDecimal newBalance = toAccount.getBalance().add(amount);
                    updateAccountBalance(toAccountId, newBalance, currency);
                }
                // create transaction record
                {
                    insertTransaction(fromAccountId, toAccountId, amount, currency, note, true);
                }
                return true;
            } else {
                // create transaction record
//            well, let's support only transactions between local accounts
//                insertTransaction(fromAccountId, toAccountId, amount, currency, note, false);
                return false;
            }
    }

    private String ACCOUNT_INSERT_SQL = "INSERT INTO TRANSACTION (FROM_ACCOUNT,TO_ACCOUNT,AMOUNT,CURRENCY,NOTE,EXECUTED) VALUES(?,?,?,?,?,?)";

    private boolean insertTransaction(String fromAccountId, String toAccountId, BigDecimal amount, String currency, String note, boolean executed) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ACCOUNT_INSERT_SQL);
            preparedStatement.setString(1,fromAccountId );
            preparedStatement.setString(2, toAccountId);
            preparedStatement.setBigDecimal(3, amount);
            preparedStatement.setString(4, currency);
            preparedStatement.setString(5, note);
            preparedStatement.setBoolean(6, executed);
            return preparedStatement.execute();
        }
    }

    private String ACCOUNT_UPDATE_SQL = "UPDATE ACCOUNT SET BALANCE = ? WHERE ACCOUNT_ID = ? AND CURRENCY= ?";

    private boolean updateAccountBalance(String accountId, BigDecimal amount, String currency) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
//            log.info("SQL updating balance (-) : " + );
            PreparedStatement preparedStatement = connection.prepareStatement(ACCOUNT_UPDATE_SQL);
            preparedStatement.setBigDecimal(1, amount);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, currency);
            return preparedStatement.execute();
        }
    }

    @Override
    @SneakyThrows
    public List<Transaction> getTransactionHistory(String accountNo) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM" +
                    " TRANSACTION WHERE FROM_ACCOUNT = ? OR TO_ACCOUNT = ?");
            preparedStatement.setString(1, accountNo);
            preparedStatement.setString(2, accountNo);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getLong("TRANSACTION_ID"),
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