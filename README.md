# Exercise 5 - XML External Entities (XXE) 
Exercise to help you understand XEE exploits and their mitigations

## Setup and Start Applications

1. check out exercise5 
   * from command line: 'git checkout exercise5'
1. start the v-bank-app in debug mode
   * with maven command:'mvn clean spring-boot:run -f ./v-bank-app/pom.xml'
1. start the attacker-app in debug mode
   * with maven command:'mvn clean spring-boot:run -f ./attacker-app/pom.xml'
   
## Brief description of the exercise
The applications in this scenario are:
* the vbank application at http://vbank.127.0.0.1.xip.io:8080 
* the attacker application at http://attack.127.0.0.1.xip.io:9090/, this holds the (malicaiout) XML payloads and DTDs
  * For the standard XXE scenario:
    * The XML payload: http://attack.127.0.0.1.xip.io:9090/xml/xxe_standard.xml 
  * For the out-of-band XXE scenario
    * The XML payload: http://attack.127.0.0.1.xip.io:9090/xml/xxe_out-of-band.xml
    * And the DTD: http://attack.127.0.0.1.xip.io:9090/xml/evil.dtd
* The victim service at: http://service.127.0.0.1.xip.io:9090/victim
    * This service should normally not be availabe to the client calling the v-bank application. 
    With the help of the XXE exploit the attacker manages to call the service and:
      * Standard XXE: Place the victim's output in the processed document and put in the note of resulting transaction.
      * Out-of-band XXE: Send the victim's output to the logger service at http://attack.127.0.0.1.xip.io:9090/log (whose input can be viewed at http://attack.127.0.0.1.xip.io:9090/viewlog)
But first you have to find the XXE vulnerability in the v-bank-app.


## Look for XXE vulnerabilities

Hint: XXE vulnerabilities are to be found where XML parsing/processing takes place.

Present vulnerabilities: ```BankController.uploadTransactions(...)``` where the processing of the uploaded XML from the home page takes place.

## Exploit to understand

For the exploit we will use the XML files downloadable from http://attack.127.0.0.1.xip.io:9090/xxe 
Please download the beolow files to a folder on your local drive:
* transactions.xml: Normal payload
* xxe_call_service.xml: XEE payload calling a service
* xxe_call_service_out-of-band.xml: XXE payload calling a service and returning results out of band

1. Test
   * Login as bob (bob / h3ll0bob) and upload transactions.xml. 
   * You should see 2 transactions in the transaction history page.

1. Standard XXE
   * Login as bob (bob / h3ll0bob) and upload transactions.xml
    
1. Out-of-Band XXE    
    * Login as bob (bob / h3ll0bob) and upload transactions.xml