package common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import javax.xml.bind.ValidationException;
import model.TransferRequest;
import org.junit.Test;

public class ValidatorTest {

  //isValidAccountId
  @Test
  public void emptyAccountId_shouldNotBeValid() {
    assertFalse("Should return account", Validator.isValidAccountId(""));
  }

  @Test
  public void positiveAccountId_shouldBeValid() {
    assertTrue("Should return account", Validator.isValidAccountId("1"));
  }

  @Test
  public void negativeAccountId_shouldNotBeValid() {
    assertFalse("Should return account", Validator.isValidAccountId("-1"));
  }

  //isValidIbanNumber
  @Test
  public void emptyAccountNumber_shouldNotBeValid() {
    assertFalse("Should return recognize invalid number", Validator.isValidIbanNumber(""));
  }

  @Test
  public void shortAccountNumber_shouldNotBeValid() {
    assertFalse("Should return recognize invalid number", Validator.isValidIbanNumber("12345"));
  }

  @Test
  public void tooLongAccountNumber_shouldNotBeValid() {
    assertFalse("Should return recognize invalid number",
        Validator.isValidIbanNumber("123456789012345678901234567890"));
  }

  @Test
  public void validAccountNumber_shouldBeValid() {
    assertTrue("Should return recognize invalid number",
        Validator.isValidIbanNumber("123456789"));
  }

  //validateTransferRequest
  @Test
  public void validateTransferRequest_correctTransferRequest_shouldNotThrowException()
      throws ValidationException {
    TransferRequest request = new TransferRequest(BigDecimal.ONE, "1234567", "12345678", "amazon");

    Validator.validateTransferRequest(request);
  }

  @Test
  public void validateTransferRequest_emptyAmount_shouldThrowException()
      throws ValidationException {
    TransferRequest request = new TransferRequest(null, "1234567", "12345678", "amazon");

    assertThrows(ValidationException.class, () -> Validator.validateTransferRequest(request));
  }

  @Test
  public void validateTransferRequest_negaticeAmount_shouldThrowException()
      throws ValidationException {
    TransferRequest request = new TransferRequest(BigDecimal.valueOf(-1), "1234567", "12345678",
        "amazon");

    assertThrows(ValidationException.class, () -> Validator.validateTransferRequest(request));
  }

  @Test
  public void validateTransferRequest_invalidSender_shouldThrowException()
      throws ValidationException {
    TransferRequest request = new TransferRequest(BigDecimal.ONE, "1", "12345678", "amazon");

    assertThrows(ValidationException.class, () -> Validator.validateTransferRequest(request));
  }

  @Test
  public void validateTransferRequest_invalidReceiver_shouldThrowException()
      throws ValidationException {
    TransferRequest request = new TransferRequest(BigDecimal.ONE, "12345678", "1", "amazon");

    assertThrows(ValidationException.class, () -> Validator.validateTransferRequest(request));
  }

  @Test
  public void validateTransferRequest_duplicatedAccounts_shouldThrowException()
      throws ValidationException {
    TransferRequest request = new TransferRequest(BigDecimal.ONE, "12345678", "12345678", "amazon");

    assertThrows(ValidationException.class, () -> Validator.validateTransferRequest(request));
  }
}
