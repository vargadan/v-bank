package com.dani.vbank.model;

import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Amount;
import com.dani.vbank.model.primitive.Currency;
import com.dani.vbank.model.primitive.Username;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
@Access(AccessType.FIELD)
public class AccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ACCOUNT_ID")
    private Long id;

    @Column(name="ACCOUNT_NO")
    private AccountNumber accountNo;

    private Username username;

    private Amount balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

}
