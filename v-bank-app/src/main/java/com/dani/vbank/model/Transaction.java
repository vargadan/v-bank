package com.dani.vbank.model;

import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Amount;
import com.dani.vbank.model.primitive.Currency;
import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "transaction")
@XmlType(name = "transaction", propOrder = {
        "fromAccountNo",
        "toAccountNo",
        "amount",
        "currency",
        "note"
})
@Entity
@NamedQueries({
        @NamedQuery(name="transactionsOfAccount", query = "select t from Transaction t where t.fromAccountNo = :accountNo or t.toAccountNo = :accountNo")
})
public class Transaction {

    @XmlElement(required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    Long transactionId;

    @XmlElement(required = true)
    @Column(name = "FROM_ACCOUNT")
    AccountNumber fromAccountNo;

    @XmlElement(required = true)
    @Column(name = "TO_ACCOUNT")
    AccountNumber toAccountNo;

    @XmlElement(required = true)
    Amount amount;

    @XmlElement(required = true)
    @Enumerated(EnumType.STRING)
    Currency currency;

    @XmlElement(required = true)
    String note;

    @XmlTransient
    boolean executed;
}
