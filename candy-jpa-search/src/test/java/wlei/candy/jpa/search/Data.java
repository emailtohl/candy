package wlei.candy.jpa.search;


import wlei.candy.jpa.search.auction.entities.Bid;
import wlei.candy.jpa.search.auction.entities.Category;
import wlei.candy.jpa.search.auction.entities.Item;
import wlei.candy.jpa.search.auction.entities.Participator;

public class Data {
  private Category category;
  private Item item;
  private Bid bid1, bid2;
  private Participator seller;
  private Participator bidder1;
  private Participator bidder2;

  public Data copy() {
    Data data = new Data();
    data.category = category.clone();
    data.item = item.clone();
    data.bid1 = bid1.clone();
    data.bid2 = bid2.clone();
    data.seller = seller.clone();
    data.bidder1 = bidder1.clone();
    data.bidder2 = bidder2.clone();
    return data;
  }

  public Category getCategory() {
    return category;
  }

  public Data setCategory(Category category) {
    this.category = category;
    return this;
  }

  public Item getItem() {
    return item;
  }

  public Data setItem(Item item) {
    this.item = item;
    return this;
  }

  public Bid getBid1() {
    return bid1;
  }

  public Data setBid1(Bid bid1) {
    this.bid1 = bid1;
    return this;
  }

  public Bid getBid2() {
    return bid2;
  }

  public Data setBid2(Bid bid2) {
    this.bid2 = bid2;
    return this;
  }

  public Participator getSeller() {
    return seller;
  }

  public Data setSeller(Participator seller) {
    this.seller = seller;
    return this;
  }

  public Participator getBidder1() {
    return bidder1;
  }

  public Data setBidder1(Participator bidder1) {
    this.bidder1 = bidder1;
    return this;
  }

  public Participator getBidder2() {
    return bidder2;
  }

  public Data setBidder2(Participator bidder2) {
    this.bidder2 = bidder2;
    return this;
  }
}
