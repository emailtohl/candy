package wlei.candy.jpa;

import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.Category;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.entities.Participator;

class Data {
  Category category;
  Item item;
  Bid bid1, bid2;
  Participator seller, bidder1, bidder2;

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
}
