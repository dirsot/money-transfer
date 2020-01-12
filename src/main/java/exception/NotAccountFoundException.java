package exception;

public class NotAccountFoundException extends Exception {

  private final String accountId;
  private final String iban;

  public NotAccountFoundException(String accountId) {
    this.accountId = accountId;
    this.iban = null;
  }

  public NotAccountFoundException(String accountId, String iban) {
    this.accountId = accountId;
    this.iban = iban;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getIban() {
    return iban;
  }
}