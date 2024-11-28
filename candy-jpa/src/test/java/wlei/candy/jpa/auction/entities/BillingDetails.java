package wlei.candy.jpa.auction.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;
import wlei.candy.jpa.UsualAuditableEntity;

@Audited
@Table(name = "AUCTION_BILLING_DETAILS")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class BillingDetails<T extends BillingDetails<T>> extends UsualAuditableEntity<T> {

  @NotNull
  protected String owner;

  protected BillingDetails() {
  }

  protected BillingDetails(String owner) {
    this.owner = owner;
  }

  public String getOwner() {
    return owner;
  }

  @SuppressWarnings("unchecked")
  public T setOwner(String owner) {
    this.owner = owner;
    return (T) this;
  }
}
