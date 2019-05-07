package com.dani.vbank.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {

    private String accountNo;

    private String ownerId;

    private BigDecimal balance;

    private String currency;

}
