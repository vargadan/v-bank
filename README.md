# exercise 1 - CSRF
1st exercise with the vulnerable e-bank

CSRF with this exercise you are going to understand CSRF and its mitigations

1. check out exercise1 
   * from command line: 'git checkout exercise1'
1. start the v-bank-app in debug mode
   * with maven command:'mvn clean spring-boot:run -f ./v-bank-app/pom.xml'
1. start the attacker-app
   * woth maven command: 'mvn clean spring-boot:run -f ./attacker-app/pom.xml'
1. open v-bank app and log in 
   1. open http://vbank.0.0.0.0.xip.io:8080/
   1. login with as 'bob' with password 'h3ll0bob
   1. click on send money and wire 1000 CHF to eve:
      * account no: 2-123456-22
      * amount: 100 
      * currency: CHF
      * note: 'Hi Eve here is you 100!'
   1. go the transactopm history page by clicking on the account number on the home page. to check the transaction and stay here.
1. now on behalf of alice we execute a CSRF attack against bob
  1. alice tricks bob into open a web page under her control:
     * http://attack.0.0.0.0.xip.io:9090/csrf
     * if you open the page source you see that it contains a pre filled form posted at the url handling transactions forms 
  1. as (little careless) bob open the above page and click on the button
  1. then go back to the transactions page and refresh it you will see that you are 1000 CHF worse off because you have been CSRF-ed
  1. in order to understand how the CSRF attack is working
     * start the Burp tool to intercept http calls
        * in the Proxy > Options tab change the proxy port from 8080 to 8181 so that it does not conflict with the v-bank application
        * change your browser's proxy to 127.0.0.1:8181
        * make sure that Intercept is ON in the Burp tool (Proxy > Intercep tab)
     * Place a debug brakepoint in *BankController.doTransfer(...)* (*BankController* class *doTransfer* method)
