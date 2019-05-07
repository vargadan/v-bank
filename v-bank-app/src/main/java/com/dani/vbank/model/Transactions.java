package com.dani.vbank.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "transactions")
public class Transactions {

    @XmlElement(required = true, name = "transaction")
    public List<Transaction> transactions;

}
