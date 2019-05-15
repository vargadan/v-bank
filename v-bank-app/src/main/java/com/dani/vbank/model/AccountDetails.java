package com.dani.vbank.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class AccountDetails {

    @Id
    @Column(name="ACCOUNT_ID")
    private String accountNo;

    private String username;

    private BigDecimal balance;

    private String currency;

}
