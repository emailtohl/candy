package wlei.candy.jpa;

import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.Category;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.entities.Participator;

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

  public Item getItem() {
    return item;
  }

  public Bid getBid1() {
    return bid1;
  }

  public Bid getBid2() {
    return bid2;
  }

  public Participator getSeller() {
    return seller;
  }

  public Participator getBidder1() {
    return bidder1;
  }

  public Participator getBidder2() {
    return bidder2;
  }
}
