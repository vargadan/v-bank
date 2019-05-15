package com.dani.vbank.model;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

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
public class Transaction {

    @XmlElement(required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    Long transactionId;

    @XmlElement(required = true)
    @Column(name = "FROM_ACCOUNT")
    String fromAccountNo;

    @XmlElement(required = true)
    @Column(name = "TO_ACCOUNT")
    String toAccountNo;

    @XmlElement(required = true)
    BigDecimal amount;

    @XmlElement(required = true)
    String currency;

    @XmlElement(required = true)
    String note;

    @XmlTransient
    boolean executed;
}
