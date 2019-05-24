# Exercise 4 - Deep modeling

Exercise to help you understand the benefits of deep modelling (aka expressive domain models)

## Setup and Start Applications

1. check out exercise4 
   * from command line: 'git checkout exercise4'
   
## Study changes compared to exercise3
* Have a look at the following classes
  * _Transaction_
  * _AccountDetails_
  * _BankController_
* What changes did happen to the classes _Transaction_ and _AccountDetails_?
  * New primitive types for introduced:
    * _AccountNumber_
    * _Amount_
    * _Currency_
*  What changes did happen to the class _BankController_?
  * validation logic removed
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
    * What are the security benefits of using custom types insted of language primitive ones? 
    * Can you think of any drawbacks?
