package wlei.candy.jpa.envers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import wlei.candy.jpa.KeyAttribute;
import wlei.candy.jpa.envers.auction.entities.Bid;
import wlei.candy.jpa.envers.auction.entities.CreditCard;
import wlei.candy.jpa.envers.auction.entities.Item;
import wlei.candy.jpa.envers.auction.entities.Participator;
import wlei.candy.share.util.JsonUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static wlei.candy.jpa.GenericEntity.*;
import static wlei.candy.jpa.SoftDeletable.PROP_DELETE_TIME;
import static wlei.candy.jpa.envers.Auditability.*;

class UsualAuditableEntityTest {

  @Test
  void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    SomeEntity se = new SomeEntity()
        .setCreateTime(now.minusMinutes(1L)).setUpdateTime(now)
        .setCreateBy("foo").setUpdateBy("bar");
    assertNotEquals(now, se.getCreateTime());
    se = new SomeEntity(se);
    assertNotNull(se.getUpdateTime());
    assertNotEquals(se.getCreateBy(), se.getUpdateBy());
    assertNotEquals(se.getCreateTime(), se.getUpdateTime());
  }

  @Test
  void preUpdate() {
    SomeEntity se = new SomeEntity();
    se.setCreateBy("foo");
    LocalDateTime now = LocalDateTime.now();
    se.setCreateTime(now);
    se.setUpdateTime(now.plusHours(1L));
    assertNotEquals(now, se.getUpdateTime());
    assertNotEquals(se.getCreateTime(), se.getUpdateTime());
  }

  @Test
  void parse() {
    CreditCard c = new CreditCard();
    c.setCardNumber("123456");
    c.setExpMonth("12");
    KeyAttribute[] keyAttributes = KeyAttribute.parse(c);
    assertEquals(1, keyAttributes.length);

    assertArrayEquals(new String[]{PROP_ID, PROP_CREATE_TIME, PROP_MOD_VER, PROP_DELETE_TIME, PROP_CREATE_BY, PROP_UPDATE_TIME, PROP_UPDATE_BY}, c.includeBasicPropertyNames());
    assertArrayEquals(new String[]{PROP_ID, PROP_CREATE_TIME, PROP_MOD_VER, PROP_DELETE_TIME, PROP_CREATE_BY, PROP_UPDATE_TIME, PROP_UPDATE_BY, "password"}, c.includeBasicPropertyNames("password"));
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
    BeanUtils.copyProperties(item1, item2, item1.includeBasicPropertyNames("name"));
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
    System.out.println(item.setCreateTime(now).setUpdateTime(now));

    assertTrue(timeEquals(item.getCreateTime(), now));
    assertTrue(timeEquals(item.getUpdateTime(), now));

    String json = JsonUtil.writeValue(item);
    item = JsonUtil.readValue(json, Item.class);
    assertTrue(timeEquals(item.getCreateTime(), now));
    assertTrue(timeEquals(item.getUpdateTime(), now));
  }

  @Test
  void closeBeforeUpdate() {
    SomeEntity e = new SomeEntity();
    e.setCloseBeforeUpdate(true);
    assertTrue(e.isCloseBeforeUpdate());
    LocalDateTime now = LocalDateTime.of(2025, 3, 15, 21, 41, 50);
    e.setUpdateBy("uuu");
    e.setUpdateTime(now);
    e.setCreateBy("www");
    e.beforeUpdate();
    assertEquals(now, e.getUpdateTime());
    assertEquals("uuu", e.getUpdateBy());
    assertEquals("www", e.getCreateBy());
  }

  private boolean timeEquals(LocalDateTime d1, LocalDateTime d2) {
    String sd1 = d1.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 19);
    String sd2 = d2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 19);
    return sd2.equals(sd1);
  }

  @Test
  void copyBasicFrom() {
    Item item = new Item()
        .setId(1L)
        .setCreateTime(LocalDateTime.now()).setModVer(1)
        .setCreateBy("xxx").setUpdateBy("yyy").setUpdateTime(LocalDateTime.now().plusHours(1L))
        .setName("foo").setDescription("desc");
    Item other = new Item().copyBasicFrom(item);
    assertEquals(1L, other.getId());
    assertEquals(1, other.getModVer());
    assertNotNull(other.getCreateTime());
    assertEquals("xxx", other.getCreateBy());
    assertEquals("yyy", other.getUpdateBy());
    assertNotNull(other.getCreateTime());
    assertNotNull(other.getUpdateTime());
    assertNull(other.getName());
    assertNull(other.getDescription());
  }

  @Test
  void includeBasicPropertyNames() {
    Item item = new Item();
    String[] props = item.includeBasicPropertyNames("name");
    assertArrayEquals(new String[]{PROP_ID, PROP_CREATE_TIME, PROP_MOD_VER, PROP_DELETE_TIME, PROP_CREATE_BY, PROP_UPDATE_TIME, PROP_UPDATE_BY, "name"}, props);
  }

  private static class SomeEntity extends UsualAuditableEntity<SomeEntity> {
    public SomeEntity() {
    }

    public SomeEntity(SomeEntity src) {
      super(src);
    }
  }
}