import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import model.Account;
import model.AccountHistory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import service.AccountService;
import spark.Request;
import spark.Response;
import spark.Route;

public class AccountRoutesTest {

  Request request;
  @Mock
  Response response;
  @Mock
  AccountService accountService;
  private AccountRoutes accountRoutes;

  @Before
  public void setUp() {
    accountService = mock(AccountService.class);
    accountRoutes = new AccountRoutes(accountService);
    request = mock(Request.class);
  }

  @Test
  public void getAllAcounts_shouldReturnAccounts() throws Exception {
    List<Account> expected = Collections.singletonList(new Account());
    when(accountService.getAllAccounts()).thenReturn(expected);

    Route a = accountRoutes.getAllAccounts();

    assertEquals("Should return all accounts", expected, a.handle(request, response));
  }

  @Test
  public void getAccountById_shouldReturnAccount() throws Exception {
    Optional<Account> expected = Optional.of(new Account());
    when(accountService.byId(1L)).thenReturn(expected);
    when(request.params(":id")).thenReturn("1");

    Route a = accountRoutes.getById();

    assertEquals("Should return account", expected, a.handle(request, response));
  }

  @Test
  public void getAccountHistory_shouldReturnHistory() throws Exception {
    List<AccountHistory> expected = Collections.singletonList(new AccountHistory());
    when(accountService.getAccountHistory(1L)).thenReturn(expected);
    when(request.params(":id")).thenReturn("1");

    Route a = accountRoutes.getAccountHistory();

    assertEquals("Should return history", expected, a.handle(request, response));
  }
}
