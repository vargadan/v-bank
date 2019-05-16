# exercise 1 - CSRF

CSRF with this exercise you are going to understand CSRF and its mitigations

## setup and start applications

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
  
## understand how the CSRF attack is working

  1. setup tools for http interception and java debuging
     * start the Burp tool to intercept http calls
        * in the Proxy > Options tab change the proxy port from 8080 to 8181 so that it does not conflict with the v-bank application
        * change your browser's proxy to 127.0.0.1:8181
        * make sure that Intercept is ON in the Burp tool (Proxy > Intercep tab)
     * Place a debug brakepoint in *BankController.doTransfer(...)* (*BankController* class *doTransfer* method)
  1. refresh the transfer page and intercept the http get request 
     * Please note the JSESSION cookie value in the Burp tool
     * Forward this and all subsequent requests
  1. go to the csrf attack page and click on the button again so that the CSRF call itself get interecpted
     * Please note the JSESSION cookie value in the Burp tool (it should be the same as the 
     * Forward the request
  1. Now the breakpoint should be hit
     * if you expand the transaction method parameter it's properties should be properly populated
     * if you resume the program (F9 in IntelliJ) the transaction will be properly executed
     
## Mitigations
* Possible mitigations against CSRF
  * protect session cookie with samesite attributes 
    * lax if normal GET requests are safe and modifications are behind POST (or PUT/DELETE)
    * strict otherwise
    * unfortunately depends on web-framework / server if possible
  * protect forms with CSRF token
    * additional token 
* for more detailed explanations please see: https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.md


## Task
* since samesite session cookies are not suppoted by the current JEE Servlet (2.3) and Spring (5.0.x) we have to revert to other methods
* Spring security support CSRF tokens out of the box, which is disabled in this exercise
* we are going to add our own Csrf filter insted to understand how this mitigation works:

@Component
@Order(1)
public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        validate((HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(request, response);
        setToken((HttpServletRequest) request, (HttpServletResponse) response);
    }

    private void validate(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if ("POST".equals(request.getMethod().toUpperCase())) {
            String csrfTokenIn = request.getParameter("_csrf");
            String csrfTokenExp = (String) request.getSession().getAttribute("csrfProtectionToken");
            if (!csrfTokenExp.equals(csrfTokenIn)) {
                throw new ServletException("Invalid CSRF token!");
            }
        }
    }

    private void setToken(HttpServletRequest request, HttpServletResponse response) {
        String csrfProtectionToken = (String) request.getSession(true).getAttribute("csrfProtectionToken");
        if (csrfProtectionToken == null) {
            csrfProtectionToken = UUID.randomUUID().toString();
            request.getSession().setAttribute("csrfProtectionToken", csrfProtectionToken);
        }
    }
}
     

