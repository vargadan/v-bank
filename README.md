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

## Exploit to understand
* The SQL in the JDBCUserDetailsService class is `"SELECT USERNAME, PASSWORD FROM USER U WHERE U.USERNAME = '" + uname + "'"`.
  This should be tweaked so to return USERNAME, PASSWORD pair with a known password:
`SELECT USERNAME, PASSWORD FROM USER U WHERE U.USERNAME = '' AND SELECT 'bob','password' --'`
using below input on login page:
  * username: ' UNION SELECT 'bob' AS USERNAME,'x' AS PASSWORD --
  * password: x
  This makes the code return a UserDetails object with the username (bob) and password (x) values set in the injected SQL snippet.
  Exploiting this SQLi vulnerability in the authentication component Alice could easily log in as Bob.
* The home page is calling a REST service at http://vbank.0.0.0.0.xip.io:8080/api/v1/account/ACCOUNT_NO where ACCOUNT_NO URL fragment is taken as the parameter of the SQL query selecting user details. This parameter is SQL injectable (due to *JDBCAccountService* class). 
  * we check if we can do UNION select on this parameter, injecting 4 additional result columns with an UNION injection into the original query: > http://vbank.0.0.0.0.xip.io:8080/api/v1/account/' UNION SELECT '1', '2','3','4' -- 
  * as this works we can use it to exfiltrate user credentials: > http://vbank.0.0.0.0.xip.io:8080/api/v1/account/' UNION SELECT USERNAME, PASSWORD, '3', '4' FROM USER --
  * since it returns only one row we have to iterate over the USER table: http://vbank.0.0.0.0.xip.io:8080/api/v1/account/' UNION SELECT USERNAME, PASSWORD,'3','4' FROM (SELECT ROWNUM() as ROW_NUM, USERNAME, PASSWORD FROM USER) WHERE ROW_NUM=1 --
  * increment ROW_NUM till it reaches the end of the USER table.

## Task
Use the transaction history page to exfiltrate data with SQL injection: http://vbank.0.0.0.0.xip.io:8080/history?accountNo=ACCOUNT_NO (where account no is the SQL injectable parameter).
* Hint: http://vbank.0.0.0.0.xip.io:8080/history?accountNo=%27%20UNION%20SELECT%20%271%27,%272%27,%273%27,%274%27,%275%27,%276%27,%277%27%20--

## Mitigations
* use a parameterized API such as prepared statements 
* use an ORM (object-relational-mapping) tool for database operations (JPA: Java Persistence Api)
* validate/sanitize/escape user input

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
1. Write a JPA based implementation for AccountService 
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

More on SQL injection prevention:
https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.md
