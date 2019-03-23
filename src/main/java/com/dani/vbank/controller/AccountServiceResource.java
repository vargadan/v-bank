package com.dani.vbank.controller;

import com.dani.vbank.service.impl.AccountServiceSQLImpl;
import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountServiceResource {

    @Autowired
    private AccountServiceSQLImpl delegate;

    @Autowired
    private DataGenerator generator;

    @RequestMapping(path = "/api/v1/account/{accountId}", produces = "application/json")
    public AccountDetails getAccountDetails(@PathVariable("accountId") String accountId) {
        AccountDetails details = delegate.getAccountDetails(accountId);
        return details;
    }

//    @RequestMapping(path = "/account/{accountId}", produces = "application/pdf")
//    @SneakyThrows
//    public @ResponseBody byte[] getAccountDetailsPdf(@PathVariable("accountId") String accountId) {
//        JasperPrint print = JasperFillManager.fillReport(this.getClass().getClassLoader()
//                        .getResourceAsStream("account_details.jasper"),
//                new HashMap(), new JRBeanCollectionDataSource(Arrays.asList(delegate.getAccountDetails(accountId))));
//        //converting DOM representation into byte array
//        return JasperExportManager.exportReportToPdf(print);
//    }

    @RequestMapping(path = "/api/v1/account/{accountId}/transactions")
    @ResponseBody
    public List<Transaction> getTransactionHistory(@PathVariable String accountId) {
        return delegate.getTransactionHistory(accountId);
    }

    @RequestMapping(path = "/api/v1/transfer", method = {RequestMethod.GET, RequestMethod.POST})
    public void transfer(@RequestParam String fromAcountId, @RequestParam String toAccountId,
                         @RequestParam BigDecimal amount, @RequestParam(defaultValue = "CHF") String currency,
                         @RequestParam String note) {
        delegate.transfer(fromAcountId, toAccountId, amount, currency, note);
    }
}