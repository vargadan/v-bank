package com.dani.vbank.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {

    String fromAccount;

    String toAccount;

    BigDecimal amount;

    String currency;

    String note;

    boolean executed;
}
