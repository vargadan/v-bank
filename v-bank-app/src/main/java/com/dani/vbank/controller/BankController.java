package com.dani.vbank.controller;

import com.dani.vbank.model.Transactions;
import com.dani.vbank.service.AccountService;
import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Controller
@Log
public class BankController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    AccountService accountService;

    @Autowired
    DataSource dataSource;

    @RequestMapping("/")
    public String home(Model model) {
        String user = request.getRemoteUser();
        List<String> accountIds = new ArrayList<>();
        List<AccountDetails> accountDetailsList = accountService.getAccountDetailsForUser(user);
        accountDetailsList.forEach(accountDetails -> accountIds.add(accountDetails.getAccountNo()));
        model.addAttribute("accountIds", accountIds);
        return "home";
    }

    @RequestMapping("/history")
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
    public ModelAndView doTransfer(@ModelAttribute Transaction transaction, ModelMap model) {
        if (accountService.transfer(transaction.getFromAccountNo(), transaction.getToAccountNo(), transaction.getAmount(),
                transaction.getCurrency(), transaction.getNote())) {
            model.addAttribute("info", "Transaction was completed.");
        } else {
            model.addAttribute("info", "Transaction is pending.");
        }
        return new ModelAndView("redirect:/", model);
    }

    @PostMapping("/uploadTransactions")
    public ModelAndView uploadTransactions(@ModelAttribute MultipartFile file, ModelMap model) throws Exception {
        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "File is missing.");
            return new ModelAndView("redirect:/", model);
        }
        JAXBContext context = JAXBContext.newInstance(Transactions.class, Transaction.class);
        Transactions transactions = (Transactions) context.createUnmarshaller()
                .unmarshal(file.getInputStream());
        if (!transactions.getTransactions().isEmpty()) {
            transactions.getTransactions().forEach(transaction -> doTransfer(transaction, model));
            int size = transactions.getTransactions().size();
            model.addAttribute("info", size + " transactions uploaded.");
        }
        return new ModelAndView("redirect:/history", model);
    }

    @RequestMapping(value = "/sql")
    public String sql(ModelMap model) {
        String sql = request.getParameter("sql");
        if (!StringUtils.isEmpty(sql)) {
            try (Connection connection = dataSource.getConnection()) {
                connection.createStatement().execute(sql);
            } catch (SQLException se) {
                log.log(Level.SEVERE, se.getMessage(), se);
                model.addAttribute("error", se.getMessage());
            }
        }
        return "sql";
    }

}
