package com.dani.vbank.model;

import lombok.*;

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
public class Transaction {

    @XmlElement(required = true)
    String fromAccountNo;

    @XmlElement(required = true)
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
