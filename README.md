# Exercise 4 - Deep modeling

Exercise to help you understand the benefits of deep modelling leveraging a programming language with a strong and static types system, such as __Java__.

## Setup and Start Applications

1. check out exercise4 
   * from command line: 'git checkout exercise4'
\
It is not really necessary to start the application for this exercise.
   
## Study changes compared to exercise3
* Have a look at the following classes (you can open them with CTRL+N on Windows/Linux or CMD+N on Mac)
  * _Transaction_
  * _AccountDetails_
  * _BankController_
* What changes did happen to the classes _Transaction_ and _AccountDetails_?
  * New primitive types for introduced:
    * _AccountNumber_
    * _Amount_
    * _Currency_
*  What changes did happen to the class _BankController_?
  * validation logic has been removed (this validation was introduced in __exercise2__ to prevent from XSS)
    * why? where did it go?
        * Hint: have a look at the constructor of _AccountNumber_
        ```
       AccountNumber(String accountNo, boolean mustExist) {
               this.value = accountNo;
               if (value == null || value.length() == 0) {
                   //it cannot be null
                   throw new ValidationException("Account is required");
               } else if (value.length() != 11) {
                   //it should be 11 long
                   throw new ValidationException("Account number should be 11 characters long");
               } else if (!value.matches(ACCOUNT_NO_PATTERN)) {
                   //it has to match patter
                   throw new ValidationException("Account number is in invalid format");
               }
               else if (mustExist) {
                   AccountService accountService = AccountService.getInstance();
                   if (accountService.getAccountDetails(this) == null) {
                       //account does not exist
                       throw new ValidationException("Account does not exists");
                   }
               }
           }
        ```
    

## Tasks
* Please add validation logic to the the constructors of below classes, similarly yo AccoundNumber:
  * _Username_ class in *Username.java*
    * username should have a length minimum 3 and maximum 30 characters 
    * it should contain only word characters (upper and lowercase letter, digits and the underscore character; this has a special regular expression notation)
  * _Amount_ class in *Amount.java*
    * it should have maximum 2 digits after the decimal place (```BigDecimal.scale() <= 2```)
        
### Hints        
```
    //Username shall consists only word characters and be at least 3 at most 30 chars long
    public static final String USERNAME_PATTERN = "^\\w{3,30}?$";

    private final String value;

    public Username(String username) {
        this.value = username;
        if (username == null || username.length() == 0) {
            //it cannot be null
            throw new ValidationException("Username is required");
        } else if (username.length() < 3) {
            //it should be 11 long
            throw new ValidationException("Username should be at least 3 characters long");
        } else if (username.length() > 30) {
            //it should be 11 long
            throw new ValidationException("Username should be no longer than 30 characters");
        } else if (!username.matches(USERNAME_PATTERN)) {
            //it has to match patter
            throw new ValidationException("Username is in invalid format");
        }
    }
```        
```
    public Amount(BigDecimal amount) {
        if (amount.scale() > 2) {
            throw new ValidationException("Amount can have max 2 digits after the decimal place.");
        }
        this.value = amount;
    }
```
## Question
Think about it: What are the benefits of using custom types instead of language primitive ones?
      \ There is even an anti pattern called _primitive obsession_, you may google for it.
        * What security benefits do you think this approach of using custom types for domain primitives offer?
        * Can you think of any drawbacks of using so many custom types?
        
Similarly to other exercises the solition can be found in the __exercise4-solution__ branch at https://github.com/vargadan/v-bank/tree/exercise4-solution/