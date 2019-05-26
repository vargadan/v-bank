package com.dani.vbank.model.primitive;

import com.dani.vbank.service.AccountService;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.Objects;

public class AccountNumber implements Serializable {

    public static final String ACCOUNT_NO_PATTERN = "^\\d{1}?-\\d{6}?-\\d{2}?$";

    private final String value;

    public AccountNumber(String accountNo) {
        this(accountNo, true);
    }

    public AccountNumber(String accountNo, boolean mustExist) {
        this.value = accountNo;
        if (value == null || value.length() == 0) {
            //it cannot be null
            throw new ValidationException("Account is required");
        } else if (value.length() != 11) {
            //it should be 11 long
            throw new ValidationException("Account number should be 11 characters long");
        } else if (!value.matches(ACCOUNT_NO_PATTERN)) {
            //it has to match patter
            throw new ValidationException("Account number is in invalid format");
        }
        else if (mustExist) {
            AccountService accountService = AccountService.getInstance();
            if (accountService.getAccountDetails(this) == null) {
                //account does not exist
                throw new ValidationException("Account does not exists");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}