package wlei.candy.jpa.auction.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import wlei.candy.jpa.UsualEntity;

import java.math.BigDecimal;

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "AUCTION_BID")
@Entity
public class Bid extends UsualEntity<Bid> {

  protected String name;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_BID_ITEM_ID"))
  protected Item item;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_BID_BIDDER_ID"))
  protected Participator bidder;

  @NotNull
  protected BigDecimal amount;

  public Bid() {
  }

  public Bid(String name, Item item, Participator bidder, BigDecimal amount) {
    this.name = name;
    this.item = item;
    this.amount = amount;
    this.bidder = bidder;
  }

  @Column(unique = true)
  public String getName() {
    return name;
  }

  public Bid setName(String name) {
    this.name = name;
    return this;
  }

  public Item getItem() {
    return item;
  }

  public Bid setItem(Item item) {
    this.item = item;
    return this;
  }

  public Participator getBidder() {
    return bidder;
  }

  public Bid setBidder(Participator bidder) {
    this.bidder = bidder;
    return this;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Bid setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }
}