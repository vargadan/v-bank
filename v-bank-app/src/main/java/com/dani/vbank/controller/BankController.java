package com.dani.vbank.controller;

import com.dani.vbank.model.TransactionForm;
import com.dani.vbank.model.Transactions;
import com.dani.vbank.model.primitive.AccountNumber;
import com.dani.vbank.model.primitive.Username;
import com.dani.vbank.service.AccountService;
import com.dani.vbank.model.AccountDetails;
import com.dani.vbank.model.Transaction;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.xml.bind.JAXBContext;
import java.util.*;
import java.util.stream.Stream;

@Controller
@Log
public class BankController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    AccountService accountService;

    @RequestMapping("/")
    public String home(Model model) {
        String user = request.getRemoteUser();
        List<String> accountIds = new ArrayList<>();
        List<AccountDetails> accountDetailsList = accountService.getAccountDetailsForUser(new Username(user));
        accountDetailsList.forEach(accountDetails -> accountIds.add(accountDetails.getAccountNo().getValue()));
        model.addAttribute("accountIds", accountIds);
        return "home";
    }

    @RequestMapping("/history")
    public String history(Model model) {
        String accountNo = request.getParameter("accountNo");
        List<Transaction> transactions = accountService.getTransactionHistory(new AccountNumber(accountNo));
        model.addAttribute("transactions", transactions);
        model.addAttribute("accountNo", accountNo);
        return "history";
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public ModelAndView transaction(@ModelAttribute TransactionForm transaction, Model model) {
        return new ModelAndView("transfer", "transaction", new TransactionForm());
    }

    @RequestMapping(value = "/doTransfer", method = RequestMethod.POST)
    public ModelAndView doTransfer(@ModelAttribute TransactionForm transactionForm, ModelMap model) {
        //only execute transfer if the transaction data is valid
        try {
            Transaction transaction = transactionForm.toTransaction();
            //transaction is always in valid state if constructed due to its domain primitive types.
            //therefore, no need to validate.
            if (accountService.transfer(transaction)) {
                model.addAttribute("info", "Transaction was completed.");
            } else {
                model.addAttribute("info", "Transaction is pending.");
            }
            return new ModelAndView("redirect:/", model);
        } catch (ValidationException ve){
            return new ModelAndView("transfer", model);
        }
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
            transactions.getTransactions().forEach(transaction -> accountService.transfer(transaction));
            int size = transactions.getTransactions().size();
            model.addAttribute("info", size + " transactions uploaded.");
        }
        return new ModelAndView("redirect:/history", model);
    }

    private boolean validateTransaction(Transaction transaction, ModelMap modelMap) {
        boolean valid = true;
        if (!SUPPORTED_CURRENCIES.contains(transaction.getCurrency())) {
            modelMap.addAttribute("currencyMsg", "Currency not supported!");
            valid = false;
        }
        if (transaction.getAmount() == null) {
            modelMap.addAttribute("amountMsg", "Transaction amount is required");
            valid = false;
        }
        return valid;
    }

    private static final Collection<String> SUPPORTED_CURRENCIES = Arrays.asList("CHF", "USD", "GBP", "EUR");



}
