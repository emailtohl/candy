package wlei.candy.jpa.auction.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "AUCTION_BANK_ACCOUNT")
@Entity
public class BankAccount extends BillingDetails<BankAccount> {

  @NotNull
  protected String account;

  @NotNull
  protected String bankname;

  @NotNull
  protected String swift;

  public BankAccount() {
    super();
  }

  public BankAccount(String owner, String account, String bankname, String swift) {
    super(owner);
    this.account = account;
    this.bankname = bankname;
    this.swift = swift;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((account == null) ? 0 : account.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BankAccount other = (BankAccount) obj;
    if (account == null) {
      return other.account == null;
    } else return account.equals(other.account);
  }

  public String getAccount() {
    return account;
  }

  public BankAccount setAccount(String account) {
    this.account = account;
    return this;
  }

  public String getBankname() {
    return bankname;
  }

  public BankAccount setBankname(String bankname) {
    this.bankname = bankname;
    return this;
  }

  public String getSwift() {
    return swift;
  }

  public BankAccount setSwift(String swift) {
    this.swift = swift;
    return this;
  }
}
