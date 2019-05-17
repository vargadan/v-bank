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
Please note that this is not an universal property of code vulnerable to SQLi, it only stands for classes in our application that is based on Java and Spring.
* 
