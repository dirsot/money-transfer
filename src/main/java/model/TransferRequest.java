package model;

import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TransferRequest {

  public BigDecimal amount;
  public String senderAccount;
  public String receiverAccount;
  public String requstOriginApplication;

  public TransferRequest(BigDecimal amount, String senderAccount, String receiverAccount,
      String requstOriginApplication) {
    this.amount = amount;
    this.senderAccount = senderAccount;
    this.receiverAccount = receiverAccount;
    this.requstOriginApplication = requstOriginApplication;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).
        append("amount", amount).
        append("senderAccount", senderAccount).
        append("receiverAccount", receiverAccount).
        append("requstOriginApplication", requstOriginApplication).
        toString();
  }
}
