package common;

import java.math.BigDecimal;
import javax.xml.bind.ValidationException;
import model.TransferRequest;
import spark.utils.StringUtils;

/**
 * Validators of user inputs.
 */
public class Validator {

  public static boolean isValidAccountId(String id) {
    return isValidNumber(id) && Long.parseLong(id) > 0;
  }

  private static boolean isValidNumber(String id) {
    if (StringUtils.isEmpty(id)) {
      return false;
    }
    try {
      Long.parseLong(id);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static void validateTransferRequest(TransferRequest transferRequest)
      throws ValidationException {
    if (transferRequest.requstOriginApplication == null || ""
        .equals(transferRequest.requstOriginApplication)) {
      throw new ValidationException("requstOriginApplication is missing");
    }
    if (transferRequest.senderAccount == null || "".equals(transferRequest.senderAccount)) {
      throw new ValidationException("Sender account number is missing");
    }

    if (transferRequest.receiverAccount == null || "".equals(transferRequest.receiverAccount)) {
      throw new ValidationException("Receiver account number is missing");
    }

    if (!isValidIbanNumber(transferRequest.senderAccount)) {
      throw new ValidationException(
          String
              .format("Provided sender number: {%s} is not valid", transferRequest.senderAccount));
    }

    if (!isValidIbanNumber(transferRequest.receiverAccount)) {
      throw new ValidationException(
          String.format("Provided sender number: {%s} is not valid",
              transferRequest.receiverAccount));
    }
    if (transferRequest.receiverAccount.equalsIgnoreCase(transferRequest.senderAccount)) {
      throw new ValidationException("Accounts should not be the same");
    }

    if (transferRequest.amount == null || transferRequest.amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValidationException("Amount value must be positive number");
    }
  }

  /**
   * Simple IBAN validation. Anything with length between 6 and 24 is valid.
   */
  static boolean isValidIbanNumber(String iban) {
    if (iban == null || iban.equals("")) {
      return false;
    }

    return iban.length() >= 6 && iban.length() <= 24;
  }
}
