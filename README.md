## Money Transfer

Implementation of a RESTful API (including data model and the backing implementation) for money transfers between accounts.

## Dependencies:
- http://sparkjava.com/
- https://www.h2database.com/
- https://github.com/google/gson
- https://www.jooq.org/

## Usage
1.Build project

    mvn clean package

2.Start server

    java -jar ./target/money-transfer.jar

## End Points

### Accounts
    GET /accounts
    GET /accounts/:id
    GET /accounts/:id/history

### Transfers
    POST /transfer
    
Example of request body:
    
    {
        "senderAccount":accountIban1,
        "receiverAccount":"accountIban2",
        "amount":1.00,
        "requstOriginApplication":"amazon"
    }

## Limitations/Todos

- Tests uses the same database as working server(H2).
- Sender and receiver account have to be in the same currency.
- Config.properties are not used in pom.xml file, for this reason configuration is duplicated. 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details