package wlei.candy.jpa;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.Category;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.entities.Participator;
import wlei.candy.jpa.auction.repo.BidRepo;
import wlei.candy.jpa.auction.repo.CategoryRepo;
import wlei.candy.jpa.auction.repo.ItemRepo;
import wlei.candy.jpa.auction.repo.ParticipatorRepo;

import java.math.BigDecimal;
import java.util.Date;

@Component
class DataStub {
  @Autowired
  ItemRepo itemRepo;
  @Autowired
  ParticipatorRepo participatorRepo;
  @Autowired
  BidRepo bidRepo;
  @Autowired
  CategoryRepo categoryRepo;

  private Data data;

  @Transactional
  public synchronized Data getData() {
    if (data != null) {
      return data.copy();
    }
    data = new Data();
    Category parent = new Category("汽车");
    categoryRepo.saveAndFlush(parent);
    data.category = new Category("轿车", parent);
    categoryRepo.saveAndFlush(data.category);

    data.seller = new Participator("张三");
    data.bidder1 = new Participator("李四");
    data.bidder2 = new Participator("王五");
    data.seller = participatorRepo.saveAndFlush(data.seller);
    data.bidder1 = participatorRepo.saveAndFlush(data.bidder1);
    data.bidder2 = participatorRepo.saveAndFlush(data.bidder2);

    data.item = new Item("轩逸21款", new Date(System.currentTimeMillis() + 216000000L), data.seller);
    data.item.setDescription("for test");
    data.item = itemRepo.saveAndFlush(data.item);

    data.bid1 = new Bid("李四的出价", data.item, data.bidder1, BigDecimal.valueOf(50000L));
    data.bid2 = new Bid("王五的出价", data.item, data.bidder2, BigDecimal.valueOf(55000L));

    data.item.getBids().add(data.bid1);
    data.item.getBids().add(data.bid2);

    data.bid1 = bidRepo.saveAndFlush(data.bid1);
    data.bid2 = bidRepo.saveAndFlush(data.bid2);

    return data.copy();
  }

}
