package wlei.candy.jpa.search;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wlei.candy.jpa.search.auction.entities.Bid;
import wlei.candy.jpa.search.auction.entities.Category;
import wlei.candy.jpa.search.auction.entities.Item;
import wlei.candy.jpa.search.auction.entities.Participator;
import wlei.candy.jpa.search.auction.repo.BidRepo;
import wlei.candy.jpa.search.auction.repo.CategoryRepo;
import wlei.candy.jpa.search.auction.repo.ItemRepo;
import wlei.candy.jpa.search.auction.repo.ParticipatorRepo;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class DataStub {
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
    data.setCategory(new Category("轿车", parent));
    categoryRepo.saveAndFlush(data.getCategory());

    data.setSeller(new Participator("张三"));
    data.setBidder1(new Participator("李四"));
    data.setBidder2(new Participator("王五"));
    data.setSeller(participatorRepo.saveAndFlush(data.getSeller()));
    data.setBidder1(participatorRepo.saveAndFlush(data.getBidder1()));
    data.setBidder2(participatorRepo.saveAndFlush(data.getBidder2()));

    data.setItem(new Item("轩逸21款", new Date(System.currentTimeMillis() + 216000000L), data.getSeller()));
    data.getItem().setDescription("for test");
    data.setItem(itemRepo.saveAndFlush(data.getItem()));

    data.setBid1(new Bid("李四的出价", data.getItem(), data.getBidder1(), BigDecimal.valueOf(50000L)));
    data.setBid2(new Bid("王五的出价", data.getItem(), data.getBidder2(), BigDecimal.valueOf(55000L)));

    data.getItem().getBids().add(data.getBid1());
    data.getItem().getBids().add(data.getBid2());

    data.setBid1(bidRepo.saveAndFlush(data.getBid1()));
    data.setBid2(bidRepo.saveAndFlush(data.getBid2()));

    return data.copy();
  }

}
