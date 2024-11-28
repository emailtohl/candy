package wlei.candy.jpa.auction.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

@Audited
@Table(name = "AUCTION_CREDIT_CARD")
@Entity
@PrimaryKeyJoinColumn(name = "CREDITCARD_ID")
public class CreditCard extends BillingDetails<CreditCard> {

  @NotNull
  @Column(unique = true)
  protected String cardNumber;

  @NotNull
  protected String expMonth;

  @NotNull
  protected String expYear;

  public CreditCard() {
    super();
  }

  public CreditCard(String owner, String cardNumber, String expMonth, String expYear) {
    super(owner);
    this.cardNumber = cardNumber;
    this.expMonth = expMonth;
    this.expYear = expYear;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((cardNumber == null) ? 0 : cardNumber.hashCode());
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
    CreditCard other = (CreditCard) obj;
    if (cardNumber == null) {
      return other.cardNumber == null;
    } else return cardNumber.equals(other.cardNumber);
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public CreditCard setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  public String getExpMonth() {
    return expMonth;
  }

  public CreditCard setExpMonth(String expMonth) {
    this.expMonth = expMonth;
    return this;
  }

  public String getExpYear() {
    return expYear;
  }

  public CreditCard setExpYear(String expYear) {
    this.expYear = expYear;
    return this;
  }
}
