package wlei.candy.jpa.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wlei.candy.jpa.KeyAttribute;
import wlei.candy.jpa.QueryParameters;
import wlei.candy.jpa.search.auction.entities.Item;
import wlei.candy.jpa.search.auction.repo.ItemRepo;
import wlei.candy.jpa.search.auction.repo.ParticipatorRepo;
import wlei.candy.jpa.tx.TxService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by helei on 2021/4/24.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConf.class)
class SearchableRepoTest {
  @Autowired
  DataStub stub;
  @Autowired
  ItemRepo itemSearchRepo;
  @Autowired
  ParticipatorRepo participatorRepo;
  @Autowired
  TxService tx;

  @Test
  void test() {
    // 先保证数据库中有数据
    Data data = stub.getData();
    String desc = "for search test";
    tx.exec(() -> itemSearchRepo.update(new QueryParameters().add("id", data.getItem().getId()), new KeyAttribute("description", desc)));
    List<Item> items = tx.exec(() -> itemSearchRepo.search(desc));
    assertFalse(items.isEmpty());
    items = tx.exec(() -> itemSearchRepo.search(desc, "description"));
    assertFalse(items.isEmpty());
    items = tx.exec(() -> itemSearchRepo.search(data.getItem().getName()));
    assertFalse(items.isEmpty());
    items = tx.exec(() -> itemSearchRepo.search(data.getSeller().getName()));
    assertFalse(items.isEmpty());
    items = tx.exec(() -> itemSearchRepo.search(data.getBidder1().getName()));
    assertFalse(items.isEmpty());
    items = tx.exec(() -> itemSearchRepo.search(data.getBidder2().getName()));
    assertFalse(items.isEmpty());

    String remarkDesc = "remark update for search";
    tx.exec(() -> participatorRepo.update(new QueryParameters().add("id", data.getSeller().getId()), new KeyAttribute("remark", remarkDesc)));
    tx.exec(() -> {
      itemSearchRepo.refreshIndex(data.getItem().getId());
      return null;
    });
    Page<Item> page = tx.exec(() -> itemSearchRepo.search(remarkDesc, PageRequest.of(0, 10, Sort.Direction.DESC, "seller.name")));
    assertFalse(page.isEmpty());
    page = tx.exec(() -> itemSearchRepo.search(remarkDesc, PageRequest.of(0, 10)));
    assertFalse(page.isEmpty());
    page = tx.exec(() -> itemSearchRepo.search(remarkDesc, PageRequest.of(0, 10), "description"));
    assertFalse(page.isEmpty());
  }
}
