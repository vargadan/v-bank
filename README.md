# Exercise 3 - SQL injection
Exercise to help you understand SQL injection and its mitigations

## Setup and Start Applications

1. check out exercise3 
   * from command line: 'git checkout exercise3'
1. start the v-bank-app in debug mode
   * with maven command:'mvn clean spring-boot:run -f ./v-bank-app/pom.xml'

## Look for SQLi vulnerabilities
Hints:
* In java classes where database operations are carried out; in such classes an instance of the javax.sql.DataSource is injected:
```
    @Autowired
    private DataSource dataSource;
```
  * Please note that this is not an universal property of code vulnerable to SQLi, it only stands for classes in our application that is based on Java and Spring.
* Where SQL is concatenated in the code before passing it onto the JDBC api.
`connection.createStatement().executeQuery("SELECT * FROM ACCOUNT ACC WHERE ACC.USERNAME  = '" + userName + "'")`

Present vulnerabilities:
* In the *JDBCUserDetailsService* class that is responsible for loading the UserDetails upon authentication
* In the *JDBCAccountService* class that is an implementation of the *AccountService* utilizing the JDBC API directly

##Exploit to understand
* The SQL in the JDBCUserDetailsService class is `"SELECT USERNAME, PASSWORD FROM USER U WHERE U.USERNAME = '" + uname + "'"`.
  This should be tweaked so to return USERNAME, PASSWORD pair with a known password:
`SELECT USERNAME, PASSWORD FROM USER U WHERE U.USERNAME = '' AND SELECT 'bob','password' --'`
using below input on login page:
  * username: ' UNION SELECT 'bob' AS USERNAME,'x' AS PASSWORD --
  * password: x
  This makes the code return a UserDetails object with the username (bob) and password (x) values set in the injected SQL snippet.
  Exploiting this SQLi vulnerability in the authentication component alice could easily log in as bob.
*   
