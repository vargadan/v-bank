package com.dani.vbank.model;

import lombok.*;

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
    private String accountNo;

    private String ownerId;

    private BigDecimal balance;

    private String currency;

}
