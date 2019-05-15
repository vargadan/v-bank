# exercise 1
1st exercise with the vulnerable e-bank

CSRF with this exercise you are going to understand CSRF and its mitigations

check out exercise1 
- from command line: git checkout exercise1

start the v-bank-app
- from command line as below:
> mvn clean spring-boot:run -f ./v-bank-app/pom.xml

start the attacker-app
- from command line as below:
> mvn clean spring-boot:run -f ./attacker-app/pom.xml
