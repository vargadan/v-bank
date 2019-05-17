package com.dani.vbank.controller;

import com.dani.vbank.model.Transactions;
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
        //only execute transfer if the transaction data is valid
        if (validateTransaction(transaction, model)) {
            //note is free text property, we convert it to HTML encoding so that it is safe(r) to handle
            String encodedNote = Encode.forHtmlContent(transaction.getNote());
            if (accountService.transfer(transaction.getFromAccountNo(), transaction.getToAccountNo(), transaction.getAmount(),
                    transaction.getCurrency(), encodedNote)) {
                model.addAttribute("info", "Transaction was completed.");
            } else {
                model.addAttribute("info", "Transaction is pending.");
            }
            return new ModelAndView("redirect:/", model);
        } else {
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
            transactions.getTransactions().forEach(transaction -> doTransfer(transaction, model));
            int size = transactions.getTransactions().size();
            model.addAttribute("info", size + " transactions uploaded.");
        }
        return new ModelAndView("redirect:/history", model);
    }

    private boolean validateTransaction(Transaction transaction, ModelMap modelMap) {
        boolean valid = true;
        try {
            validateAccountNo(transaction.getFromAccountNo());
        } catch(ValidationException e) {
            modelMap.addAttribute("fromAccountNoMsg", e.getMessage());
            valid = false;
        }
        try {
            validateAccountNo(transaction.getToAccountNo());
        } catch(ValidationException e) {
            modelMap.addAttribute("toAccountNoMsg", e.getMessage());
            valid = false;
        }
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

    private static final String ACCOUNT_NO_PATTERN = "^\\d{1}?-\\d{6}?-\\d{2}?$";

    private void validateAccountNo(String accountNo) throws ValidationException {
        if (accountNo == null || accountNo.trim().length() == 0) {
            //it cannot be null
            throw new ValidationException("Account is required");
        } else if (accountNo.trim().length() != 11) {
            //it should be 11 long
            throw new ValidationException("Account number should be 11 characters long");
        } else if (!accountNo.matches(ACCOUNT_NO_PATTERN)) {
            //it has to match patter
            throw new ValidationException("Account number is in invalid format");
        } else if (accountService.getAccountDetails(accountNo) == null) {
            //account does not exist
            throw new ValidationException("Account does not exists");
        }
        //ok all validations passed
    }

}
