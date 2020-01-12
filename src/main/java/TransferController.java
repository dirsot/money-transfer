import common.JsonTransformer;
import common.SuccessMessage;
import exception.NotAccountFoundException;
import javax.xml.bind.ValidationException;
import org.jooq.exception.DataAccessException;
import service.AccountService;

import static spark.Spark.*;

public class TransferController {

  private static final JsonTransformer json = new JsonTransformer();

  public static void main(String[] args) {
    AccountService accountService = new AccountService();
    AccountRoutes accountRoutes = new AccountRoutes(accountService);

    get("/", (req, res) -> new SuccessMessage("You can start sending payments."), json);
    addAccountRoutes(accountRoutes);
    addTransferRoutes(accountRoutes);

    after((request, response) -> response.type("application/json"));
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
