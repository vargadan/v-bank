package com.dani.vbank.controller;

import com.dani.vbank.service.AccountService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@Log
public class DataGenerator {

    public static String BOB_ACCOUNT_1 = "1-987654-1";
    public static String EVE_ACCOUNT_1 = "3-783702-2";
    public static String ALICE_ACCOUNT_1 = "2-54239-2";

    @Autowired
    private AccountService accountService;

    @SneakyThrows
    @RequestMapping(path = "/addTransactions")
    public void addRandomTransactions(int records) {
        Random r = new Random();
        List<String> sqls = new ArrayList<>();
        r.ints().filter(i -> i > 0 && i < 10000).limit(records).forEach(
                amount -> {
                    boolean fromBob = r.nextBoolean();
                    String fromAccount = fromBob ? BOB_ACCOUNT_1 : EVE_ACCOUNT_1;
                    String toAccount = fromBob ?  EVE_ACCOUNT_1 : BOB_ACCOUNT_1;
                    String note = amount + " CHF from " + (fromBob ? "Bob to Eve" : "Eve to Bob");
                    log.info(note);
                    accountService.transfer(fromAccount, toAccount, new BigDecimal(amount), "CHF", note);
                }
        );
    }

}
