package com.dani.vbank.model;

import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Amount;
import com.dani.vbank.model.primitive.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionForm {

    String fromAccountNo;

    String toAccountNo;

    String amount;

    String currency;

    String note;

    public Transaction toTransaction() {
        AccountNumber fromAccountNumber = new AccountNumber(this.fromAccountNo);
        AccountNumber toAccountNumber = new AccountNumber(this.toAccountNo);
        Amount amount = new Amount(this.amount);
        Currency currency = Currency.valueOf(this.currency);
        return new Transaction(null, fromAccountNumber, toAccountNumber, amount, currency, this.note, false);
    }
}
