package wlei.candy.jpa.envers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;
import wlei.candy.jpa.KeyAttribute;
import wlei.candy.jpa.QueryParameters;
import wlei.candy.jpa.envers.auction.entities.AuctionType;
import wlei.candy.jpa.envers.auction.entities.Item;
import wlei.candy.jpa.envers.auction.repo.ItemRepo;
import wlei.candy.jpa.tx.TxService;
import wlei.candy.share.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static wlei.candy.jpa.GenericEntity.PROP_CREATE_TIME;
import static wlei.candy.jpa.GenericEntity.PROP_ID;
import static wlei.candy.jpa.envers.Auditability.PROP_CREATE_BY;

// SpringExtension与Junit 5 jupiter 的@ExtendWith注释一起使用，用于集成SpringTestContext和Junit5 Jupiter测试
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConf.class)
@ActiveProfiles(/*SpringConf.POSTGRES*/)
class AuditableRepositoryCRUDTest {
  @Autowired
  DataStub stub;
  @Autowired
  ItemRepo itemRepo;
  @Autowired
  TxService tx;
  private Data data;
  private long id;
  private String name;

  @BeforeEach
  void setUp() {
    data = stub.getData();
    Item item = new Item();
    name = String.valueOf(new Random().nextLong());
    item.setName(name);
    item.setSeller(data.getSeller());
    item.setAuctionEnd(plusNow(5));
    item.setBuyNowPrice(new BigDecimal(100));
    id = tx.exec(() -> itemRepo.add(item)).getId();
  }

  @AfterEach
  void clear() {
    tx.exec(() -> {
      itemRepo.delete(id);
      return null;
    });
    // 测试审计历史版本的功能
    List<RevTuple<Long, Item, Item>> revisions = tx.exec(() -> itemRepo.getRevisions(id));
    assertFalse(revisions.isEmpty());
    assertTrue(StringUtils.hasText(revisions.iterator().next().getEntity().getUpdateBy()));
    assertNotNull(revisions.iterator().next().getEntity().getUpdateTime());
    long count = revisions.size();
    assertTrue(count > 1);

    // 删除版本
    RevTuple<Long, Item, Item> delRev = revisions.stream().filter(rev -> rev.getRevisionType() == RevisionType.DEL).findFirst().orElseThrow(AssertionError::new);
    // 即便是删除的版本，也需要保留其对象初始化时的约束，例如auctionType不能为空
    assertEquals(AuctionType.HIGHEST_BID, delRev.getEntity().getAuctionType());
  }

  /**
   * 简单的保存新的实例
   */
  @Test
  void read() {
    BiFunction<CriteriaBuilder, Root<Item>, List<Predicate>> supplement = (b, r) -> Collections.singletonList(b.greaterThan(r.get(Item.PROP_CREATE_TIME), LocalDateTime.now().minusMinutes(1L)));
    Optional<Item> o = tx.exec(() -> itemRepo.get(id));
    assertTrue(o.isPresent());

    int count = tx.exec(() -> itemRepo.count(this::getPredicate));
    assertTrue(count > 0);

    count = tx.exec(() -> itemRepo.count(new QueryParameters().add("name", name).setSupplement(supplement)));
    assertTrue(count > 0);

    count = tx.exec(() -> itemRepo.count(this::getPredicate));
    assertTrue(count > 0);

    List<Item> items = tx.exec(() -> itemRepo.query(new QueryParameters().add("name", name).setSupplement(supplement)));
    assertEquals(name, items.stream().findFirst().orElseThrow(AssertionError::new).getName());

    items = tx.exec(() -> itemRepo.query(this::getPredicate));
    assertEquals(name, items.stream().findFirst().orElseThrow(AssertionError::new).getName());

    Page<Item> page = tx.exec(() -> itemRepo.query(new QueryParameters().add("name", name).setSupplement(supplement), PageRequest.of(0, 10)));
    assertFalse(page.isEmpty());

    page = tx.exec(() -> itemRepo.query(this::getPredicate, PageRequest.of(0, 10)));
    assertFalse(page.isEmpty());

  }

  private Optional<Predicate> getPredicate(CriteriaBuilder b, Root<Item> r) {
    Predicate and = b.and(b.equal(r.get("id"), id), b.equal(r.get("name"), name));
    return Optional.of(and);
  }

  @Test
  void update() {
    tx.exec(() -> {
      QueryParameters params = new QueryParameters().add("name", "轩逸21款");
      int count = itemRepo.update(params, new KeyAttribute("description", "desc 5"));
      assertTrue(count > 0);
      return count;
    });
  }

  // 测试不可变
  @Test
  void immutabilityTest() {
    final LocalDateTime t1 = LocalDateTime.of(2024, 11, 28, 12, 0, 0, 0);
    final LocalDateTime t2 = LocalDateTime.of(2024, 11, 29, 12, 0, 0, 0);
    final LocalDateTime t3 = LocalDateTime.of(2024, 11, 30, 12, 0, 0, 0);
    Item i = new Item().setCreateTime(t1).setCreateBy("foo").setName("xxy").setDescription("desc").setSeller(data.getSeller());
    final Long _id = tx.exec(() -> itemRepo.add(i).getId());
    tx.exec(() -> {
      Item item = itemRepo.get(_id).orElseThrow(IllegalArgumentException::new);
      item.setCreateTime(t2);
      item.setCreateBy("bar");
      item.setDescription("update desc");
      return null;
    });
    // createTime被设置为：updatable = false，所以update时，createTime不会更新，仍然是原来的创建时间
    assertNotEquals(t2, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertNotEquals("bar", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateBy()));
    assertEquals(t1, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertEquals("foo", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateBy()));
    // 但是description会被更新
    assertEquals("update desc", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getDescription()));

    tx.exec(() -> {
      QueryParameters params = new QueryParameters().add(PROP_ID, _id);
      int count = itemRepo.update(params, new KeyAttribute(PROP_CREATE_TIME, t3), new KeyAttribute(PROP_CREATE_BY, "fuz"));
      assertTrue(count > 0);
      return count;
    });
    // 但是指定更新的这个方法不受限制，createTime被更改
    assertNotEquals(t1, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertNotEquals(t2, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertEquals(t3, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));

    assertNotEquals("foo", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateBy()));
    assertNotEquals("bar", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateBy()));
    assertEquals("fuz", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateBy()));
  }

  @Test
  void delete() {
    Item i = new Item();
    i.setName("foo_bar");
    i.setSeller(data.getSeller());
    tx.exec(() -> itemRepo.add(i));
    int count = tx.exec(() -> itemRepo.delete(new QueryParameters().add("name", "foo_bar")));
    assertTrue(count > 0);
  }

  private Date plusNow(int days) {
    LocalDate now = LocalDate.now();
    LocalDate plus = now.plusDays(days);
    return DateUtil.toDate(plus);
  }
}
