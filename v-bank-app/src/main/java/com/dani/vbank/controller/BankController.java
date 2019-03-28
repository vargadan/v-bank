package com.dani.vbank.controller;

import com.dani.vbank.service.AccountService;
import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log
public class BankController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    AccountService accountService;

    @RequestMapping("/")
    @SneakyThrows
    public String home(Model model) {
        String user = request.getRemoteUser();
        List<String> accountIds = new ArrayList<>();
        List<AccountDetails> accountDetailsList = accountService.getAccountDetailsForUser(user);
        accountDetailsList.forEach(accountDetails -> accountIds.add(accountDetails.getAccountNo()));
        model.addAttribute("accountIds", accountIds);
        return "home";
    }

    @RequestMapping("/history")
    @SneakyThrows
    public String history(Model model) {
        String accountNo = request.getParameter("accountNo");
        List<Transaction> transactions = accountService.getTransactionHistory(accountNo);
        model.addAttribute("transactions", transactions);
        model.addAttribute("accountNo", accountNo);
        return "history";
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public ModelAndView transaction(@ModelAttribute Transaction transaction, Model model) {
        return new ModelAndView("transfer", "transaction", new Transaction());
    }

    @RequestMapping(value = "/doTransfer", method = RequestMethod.POST)
    public ModelAndView doTransfer(@ModelAttribute Transaction transaction, Model model) {
        if (accountService.transfer(transaction.getFromAccount(), transaction.getToAccount(), transaction.getAmount(),
                transaction.getCurrency(), transaction.getNote())) {
            model.addAttribute("message", "Transaction was completed.");
        } else {
            model.addAttribute("message", "Transaction is pending.");
        }
        return new ModelAndView("redirect:/", model.asMap());
    }
}
