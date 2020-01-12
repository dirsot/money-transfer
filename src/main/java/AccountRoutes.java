import com.google.gson.Gson;
import common.ResponseMessage;
import common.Validator;
import exception.NotAccountFoundException;
import java.util.Optional;
import model.Account;
import model.TransferRequest;
import service.AccountService;
import spark.Route;

/**
 * Routes definition available in server.
 */
class AccountRoutes {

  private AccountService accountService;

  AccountRoutes(AccountService accountService) {
    this.accountService = accountService;
  }

  Route getAllAccounts() {
    return (request, response) -> accountService.getAllAccounts();
  }

  Route getById() {
    return (req, res) -> {
      String id = req.params(":id");
      if (!Validator.isValidAccountId(id)) {
        throw new NotAccountFoundException(id);
      }

      Optional<Account> account = accountService.byId(Long.parseLong(id));
      if (!account.isPresent()) {
        throw new NotAccountFoundException(id);
      }
      return account;
    };
  }

  Route getAccountHistory() {
    return (req, res) -> {
      String id = req.params(":id");
      if (!Validator.isValidAccountId(id)) {
        throw new NotAccountFoundException(id);
      }

      return accountService.getAccountHistory(Long.parseLong(id));
    };
  }

  Route processTransfer() {
    return (req, res) -> {
      Gson gson = new Gson();
      TransferRequest transferRequest = gson.fromJson(req.body(), TransferRequest.class);
      Validator.validateTransferRequest(transferRequest);

      accountService.processTransfer(transferRequest);

      return new ResponseMessage( "Transfer completed");
    };
  }
}
