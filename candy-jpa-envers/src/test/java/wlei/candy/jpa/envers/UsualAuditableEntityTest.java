package wlei.candy.jpa.envers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import wlei.candy.jpa.KeyAttribute;
import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.CreditCard;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.entities.Participator;
import wlei.candy.share.util.JsonUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static wlei.candy.jpa.Auditability.*;
import static wlei.candy.jpa.GenericEntity.PROP_CREATE_TIME;
import static wlei.candy.jpa.GenericEntity.PROP_ID;

class UsualAuditableEntityTest {

  @Test
  void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    SomeEntity se = new SomeEntity()
        .setCreateTime(now.minusMinutes(1L)).setModifyTime(now)
        .setCreateBy("foo").setModifyBy("bar");
    assertNotEquals(now, se.getCreateTime());
    se = new SomeEntity(se);
    assertNotNull(se.getModifyTime());
    assertNotEquals(se.getCreateBy(), se.getModifyBy());
    assertNotEquals(se.getCreateTime(), se.getModifyTime());
  }

  @Test
  void preUpdate() {
    SomeEntity se = new SomeEntity();
    se.setCreateBy("foo");
    LocalDateTime now = LocalDateTime.now();
    se.setCreateTime(now);
    se.setModifyTime(now.plusHours(1L));
    assertNotEquals(now, se.getModifyTime());
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
    item = JsonUtil.readValue(json, Item.class);
    assertTrue(timeEquals(item.getCreateTime(), now));
    assertTrue(timeEquals(item.getModifyTime(), now));
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