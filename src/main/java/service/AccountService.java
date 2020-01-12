package service;

import static org.jooq.money.db.h2.Tables.ACCOUNT;
import static org.jooq.money.db.h2.Tables.ACCOUNT_HISTORY;

import common.GlobalSettings;
import db.H2Database;
import exception.NotAccountFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import model.Account;
import model.AccountHistory;
import model.TransferRequest;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class AccountService {

  private final static Logger LOGGER = Logger.getLogger(AccountService.class.getName());
  private DSLContext context;

  private DSLContext getContext() throws IOException, SQLException {
    if (context == null) {
      if (new GlobalSettings().isTestDataBaseInUse()) {
        LOGGER.info("Test database in use.");
        context = DSL.using(new H2Database().getConnection(), SQLDialect.H2);
      } else {
        //TODO: Change to real database.
        LOGGER.info("Real database in use.");
        context = DSL.using(new H2Database().getConnection(), SQLDialect.H2);
      }
    }
    return context;
  }

  public List<Account> getAllAccounts() throws IOException, SQLException {
    return getContext().select().from(ACCOUNT).fetchInto(Account.class);
  }

  public Optional<Account> byId(Long id) throws IOException, SQLException {
    return Optional.ofNullable(getContext().select().from(ACCOUNT).where(ACCOUNT.ID.eq(id))
        .fetchAnyInto(Account.class));
  }

  public List<AccountHistory> getAccountHistory(Long id) throws IOException, SQLException {
    return getContext().select().from(ACCOUNT_HISTORY).where(ACCOUNT_HISTORY.ACCOUNT_ID.eq(id))
        .fetchInto(AccountHistory.class);
  }

  public void processTransfer(TransferRequest request) throws IOException, SQLException {
    LOGGER.info(String.format("Money transfer:{%s}", request));
    getContext().transactionResult(configuration -> {
      Long accountFromId = DSL.using(configuration)
          .select(ACCOUNT.ID)
          .from(ACCOUNT)
          .where(ACCOUNT.IBAN.like(request.senderAccount))
          .fetchAnyInto(Long.class);
      validateAccount(request.senderAccount, accountFromId);

      Long accountToId = DSL.using(configuration)
          .select(ACCOUNT.ID)
          .from(ACCOUNT)
          .where(ACCOUNT.IBAN.like(request.receiverAccount))
          .fetchAnyInto(Long.class);
      validateAccount(request.receiverAccount, accountToId);

      DSL.using(configuration)
          .update(ACCOUNT)
          .set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.minus(request.amount))
          .where(ACCOUNT.ID.eq(accountFromId))
          .execute();

      DSL.using(configuration)
          .update(ACCOUNT)
          .set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.plus(request.amount))
          .where(ACCOUNT.ID.eq(accountToId))
          .execute();

      int historyAccount1 = DSL.using(configuration)
          .insertInto(ACCOUNT_HISTORY, ACCOUNT_HISTORY.ACCOUNT_ID, ACCOUNT_HISTORY.AMOUNT,
              ACCOUNT_HISTORY.ORIGIN)
          .values(accountFromId, request.amount.negate(), request.requstOriginApplication)
          .execute();

      int historyAccount2 = DSL.using(configuration)
          .insertInto(ACCOUNT_HISTORY, ACCOUNT_HISTORY.ACCOUNT_ID, ACCOUNT_HISTORY.AMOUNT,
              ACCOUNT_HISTORY.ORIGIN)
          .values(accountToId, request.amount, request.requstOriginApplication)
          .execute();
      if (historyAccount1 != 1 || historyAccount2 != 1) {
        throw new SQLException("Money transfer failed.");
      }
      return null;
    });
  }

  private void validateAccount(String iban, Long accountId) throws NotAccountFoundException {
    if (accountId == null || accountId < 1L) {
      throw new NotAccountFoundException(accountId == null ? null : Long.toString(accountId), iban);
    }
  }
}
