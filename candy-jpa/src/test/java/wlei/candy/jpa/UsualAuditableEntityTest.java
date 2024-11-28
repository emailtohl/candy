package wlei.candy.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.CreditCard;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.entities.Participator;
import wlei.candy.share.util.JsonUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static wlei.candy.jpa.Auditability.*;
import static wlei.candy.jpa.GenericEntity.PROP_ID;

class UsualAuditableEntityTest {

  @Test
  void prePersist() {
    SomeEntity se = new SomeEntity().setCreateTime(LocalDateTime.now().minusMinutes(1L));
    se.prePersist();
    assertNotEquals(LocalDateTime.now(), se.getCreateTime());
    se = new SomeEntity(se);
    assertNotNull(se.getModifyTime());
    assertEquals(se.getCreateBy(), se.getModifyBy());
    assertEquals(se.getCreateTime(), se.getModifyTime());
    assertEquals("id", PROP_ID);

    assertNotNull(se.getCreateBy());
    assertNotNull(se.getModifyBy());
  }

  @Test
  void preUpdate() {
    SomeEntity se = new SomeEntity();
    se.setCreateBy("foo");
    se.setCreateTime(LocalDateTime.now());
    se.setModifyTime(LocalDateTime.now().plusHours(1L));
    se.preUpdate();
    assertNotEquals(LocalDateTime.now(), se.getModifyTime());
    assertNotEquals(se.getCreateTime(), se.getModifyTime());
  }

  @Test
  void parse() {
    CreditCard c = new CreditCard();
    c.setCardNumber("123456");
    c.setExpMonth("12");
    KeyAttribute[] keyAttributes = KeyAttribute.parse(c);
    assertEquals(1, keyAttributes.length);

    assertArrayEquals(new String[]{PROP_ID, PROP_CREATE_TIME, PROP_CREATE_BY, PROP_MODIFY_TIME, PROP_MODIFY_BY}, c.includeBasePropertyNames());
    assertArrayEquals(new String[]{PROP_ID, PROP_CREATE_TIME, PROP_CREATE_BY, PROP_MODIFY_TIME, PROP_MODIFY_BY, "password"}, c.includeBasePropertyNames("password"));
  }

  @Test
  void testCopyProperties() {
    Item item1 = new Item();
    item1.setId(110L);
    item1.setName("foo");
    item1.setDescription("hello world");
    item1.setSeller(new Participator("bar"));
    item1.getBids().add(new Bid("baz", item1, new Participator("fuz"), new BigDecimal(100)));

    Item item2 = new Item();
    item2.setId(111L);
    item2.setName("xxx");
    BeanUtils.copyProperties(item1, item2, item1.includeBasePropertyNames("name"));
    assertEquals(111, item2.getId());
    assertEquals("xxx", item2.getName());
    assertEquals("hello world", item2.getDescription());
    assertEquals("bar", item2.getSeller().getName());
    assertFalse(item2.getBids().isEmpty());
  }

  @Test
  void localDateTimeTest() {
    Item item = new Item();
    item.setId(110L);
    item.setName("foo");
    item.setDescription("hello world");
    item.setSeller(new Participator("bar"));
    item.getBids().add(new Bid("baz", item, new Participator("fuz"), new BigDecimal(100)));

    LocalDateTime now = LocalDateTime.now();
    System.out.println(item.setCreateTime(now).setModifyTime(now));

    assertTrue(timeEquals(item.getCreateTime(), now));
    assertTrue(timeEquals(item.getModifyTime(), now));

    String json = JsonUtil.writeValue(item);
    System.out.println(json);
    item = JsonUtil.readValue(json, Item.class);
    assertTrue(timeEquals(item.getCreateTime(), now));
    assertTrue(timeEquals(item.getModifyTime(), now));
  }

  private boolean timeEquals(Date d1, LocalDateTime d2) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    String sd1 = format.format(d1);
    String sd2 = d2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    return sd2.contains(sd1);
  }

  private boolean timeEquals(LocalDateTime d1, LocalDateTime d2) {
    String sd1 = d1.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 19);
    String sd2 = d2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 19);
    return sd2.equals(sd1);
  }

  private static class SomeEntity extends UsualAuditableEntity<SomeEntity> {
    public SomeEntity() {
    }

    public SomeEntity(SomeEntity src) {
      super(src);
    }
  }
}