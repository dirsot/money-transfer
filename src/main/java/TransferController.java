import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import common.JsonTransformer;
import exception.NotAccountFoundException;
import javax.xml.bind.ValidationException;
import org.jooq.exception.DataAccessException;
import service.AccountService;

public class TransferController {

  private static final JsonTransformer json = new JsonTransformer();

  public static void main(String[] args) {
    AccountService accountService = new AccountService();
    AccountRoutes accountRoutes = new AccountRoutes(accountService);

    get("/", (req, res) -> "You can start sending payments.", json);
    addAccountRoutes(accountRoutes);
    addTransferRoutes(accountRoutes);

    mapExceptions();
  }

  private static void addAccountRoutes(AccountRoutes accountRoutes) {
    get("/accounts", accountRoutes.getAllAccounts(), json);
    get("/accounts/:id", accountRoutes.getById(), json);
    get("/accounts/:id/history", accountRoutes.getAccountHistory(), json);
  }

  private static void addTransferRoutes(AccountRoutes accountRoutes) {
    post("/transfer", accountRoutes.processTransfer(), json);
  }

  private static void mapExceptions() {
    exception(NotAccountFoundException.class, (e, request, response) -> {
      response.status(404);
      response.body(String.format("Account with id {%s} not found", e.getAccountId()));
    });

    exception(ValidationException.class, (e, request, response) -> {
      response.status(400);
      response.body(e.getMessage());
    });

    exception(DataAccessException.class, (e, request, response) -> {
      if (e.getCause() instanceof NotAccountFoundException) {
        NotAccountFoundException exception = (NotAccountFoundException) e.getCause();
        response.status(404);
        response.body(String.format("Account with IBAN {%s} not found", exception.getIban()));
      } else {
        throw e;
      }
    });
  }
}
