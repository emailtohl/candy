package wlei.candy.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wlei.candy.jpa.auction.entities.Category;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.repo.CategoryRepo;
import wlei.candy.jpa.auction.repo.ItemRepo;
import wlei.candy.jpa.cache.CacheService;
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
import static wlei.candy.jpa.SoftDeletable.PROP_DELETE_TIME;

// SpringExtension与Junit 5 jupiter 的@ExtendWith注释一起使用，用于集成SpringTestContext和Junit5 Jupiter测试
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConf.class)
@ActiveProfiles(/*SpringConf.POSTGRES*/)
class UsualRepositoryCRUDTest {
  @Autowired
  DataStub stub;
  @Autowired
  ItemRepo itemRepo;
  @Autowired
  CategoryRepo categoryRepo;
  @Autowired
  TxService tx;
  @Autowired
  CacheService cacheService;
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

  // 测试指定值更新
  @Test
  void update() {
    tx.exec(() -> {
      QueryParameters params = new QueryParameters().add("name", "轩逸21款");
      int count = itemRepo.update(params, new KeyAttribute("description", "desc 5"));
      assertTrue(count > 0);
      return count;
    });
    Item item = cacheService.get(Item.class, data.getItem().getId());
    assertEquals("desc 5", item.getDescription());
  }

  // 测试乐观锁
  @Test
  void optlock() {
    tx.exec(() -> {
      itemRepo.get(id).ifPresent(i -> i.setDescription("desc 6"));
      return null;
    });
    assertEquals("desc 6", tx.exec(() -> itemRepo.get(id).orElseThrow(IllegalArgumentException::new).getDescription()));
    assertTrue(tx.exec(() -> itemRepo.get(id).orElseThrow(IllegalArgumentException::new).getModVer()) > 0);
  }

  // 测试不可变
  @Test
  void immutabilityTest() {
    final LocalDateTime t1 = LocalDateTime.of(2024, 11, 28, 12, 0, 0, 0);
    final LocalDateTime t2 = LocalDateTime.of(2024, 11, 29, 12, 0, 0, 0);
    final LocalDateTime t3 = LocalDateTime.of(2024, 11, 30, 12, 0, 0, 0);
    Item i = new Item().setCreateTime(t1).setName("xxy").setDescription("desc").setSeller(data.getSeller());
    final Long _id = tx.exec(() -> itemRepo.add(i).getId());
    tx.exec(() -> {
      Item item = itemRepo.get(_id).orElseThrow(IllegalArgumentException::new);
      item.setCreateTime(t2);
      item.setDescription("update desc");
      return null;
    });
    // createTime被设置为：updatable = false，所以update时，createTime不会更新，仍然是原来的创建时间
    assertNotEquals(t2, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertEquals(t1, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    // 但是description会被更新
    assertEquals("update desc", tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getDescription()));

    tx.exec(() -> {
      QueryParameters params = new QueryParameters().add(PROP_ID, _id);
      int count = itemRepo.update(params, new KeyAttribute(PROP_CREATE_TIME, t3));
      assertTrue(count > 0);
      return count;
    });
    // 但是指定更新的这个方法不受限制，createTime被更改
    assertNotEquals(t1, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertNotEquals(t2, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
    assertEquals(t3, tx.exec(() -> itemRepo.get(_id).orElseThrow(IllegalArgumentException::new).getCreateTime()));
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

  @Test
  void testSoftDelete() {
    Long iid = tx.exec(() -> itemRepo.add(new Item().setName("Hello").setSeller(data.getSeller())).getId());
    Long[] ids = tx.exec(() -> {
      Item i = itemRepo.get(iid).orElseThrow(IllegalArgumentException::new);
      Category c1 = new Category().setName("c1");
      c1.getItems().add(i);
      i.getCategories().add(c1);
      categoryRepo.add(c1);

      Category c2 = new Category().setName("c2").setParent(c1);
      c2.getItems().add(i);
      i.getCategories().add(c2);
      categoryRepo.add(c2);
      return new Long[]{c1.getId(), c2.getId()};
    });

    List<Category> all = tx.exec(() -> categoryRepo.findAll());
    assertFalse(all.isEmpty());
    assertTrue(all.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertTrue(all.stream().anyMatch(c -> "c2".equals(c.getName())));

    LocalDateTime deleteTime = LocalDateTime.of(2024, 12, 11, 20, 2, 11);

    // 先标记删除父节点
    tx.exec(() -> categoryRepo.get(ids[0]).orElseThrow(IllegalArgumentException::new).setDeleteTime(deleteTime));
    // 查询所有
    all = tx.exec(() -> categoryRepo.query(new QueryParameters()));
    // 能查到结果
    assertFalse(all.isEmpty());
    // 但是标记删除的不会在此结果中
    assertFalse(all.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertTrue(all.stream().anyMatch(c -> "c2".equals(c.getName())));
    // 根据id还是能查得到，只是Query查询会排除掉标记删除的结果
    Optional<Category> o = tx.exec(() -> categoryRepo.get(ids[0]));
    assertTrue(o.isPresent());

    // 对另一个做同样的标记删除，然后做同样的预期
    tx.exec(() -> categoryRepo.get(ids[1]).orElseThrow(IllegalArgumentException::new).setDeleteTime(deleteTime));

    all = tx.exec(() -> categoryRepo.query(new QueryParameters()));
    assertFalse(all.isEmpty());
    assertFalse(all.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertFalse(all.stream().anyMatch(c -> "c2".equals(c.getName())));
    o = tx.exec(() -> categoryRepo.get(ids[1]));
    assertTrue(o.isPresent());

    // 对于分页查询也支持
    Page<Category> page = tx.exec(() -> categoryRepo.query(new QueryParameters(), Pageable.ofSize(100)));
    assertFalse(page.isEmpty());
    assertFalse(page.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertFalse(page.stream().anyMatch(c -> "c2".equals(c.getName())));

    // 针对删除字段的查询
    all = tx.exec(() -> categoryRepo.query(new QueryParameters().add(PROP_DELETE_TIME, deleteTime)));
    assertTrue(all.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertTrue(all.stream().anyMatch(c -> "c2".equals(c.getName())));

    all = tx.exec(() -> categoryRepo.query(new QueryParameters().setSupplement((b, r) -> Collections.singletonList(b.equal(r.get(PROP_DELETE_TIME), deleteTime)))));
    assertTrue(all.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertTrue(all.stream().anyMatch(c -> "c2".equals(c.getName())));

    all = tx.exec(() -> categoryRepo.query(new QueryParameters().setSupplement((b, r) -> Collections.singletonList(b.lessThanOrEqualTo(r.get(PROP_DELETE_TIME), LocalDateTime.of(2024, 12, 11, 21, 2, 11))))));
    assertTrue(all.stream().anyMatch(c -> "c1".equals(c.getName())));
    assertTrue(all.stream().anyMatch(c -> "c2".equals(c.getName())));

    // 最后删除测试数据
    tx.exec(() -> {
      itemRepo.delete(iid);
      categoryRepo.delete(ids[1]);
      categoryRepo.delete(ids[0]);
      return null;
    });
  }

  private Date plusNow(int days) {
    LocalDate now = LocalDate.now();
    LocalDate plus = now.plusDays(days);
    return DateUtil.toDate(plus);
  }
}
