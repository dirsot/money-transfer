package model;

import java.math.BigDecimal;
import java.util.Date;

public class AccountHistory {

  public long id;
  public long accountId;
  public Date date;
  public BigDecimal amount;
  public String origin;
}
