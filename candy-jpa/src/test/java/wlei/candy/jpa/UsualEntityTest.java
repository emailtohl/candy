package wlei.candy.jpa;

import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static wlei.candy.jpa.GenericEntity.*;
import static wlei.candy.jpa.SoftDeletable.PROP_DELETE_TIME;

/**
 * Author: HeLei
 * Date: 2024/11/30
 */
class UsualEntityTest {

  @Test
  void copyBasicFrom() {
    LocalDateTime deleteTime = LocalDateTime.of(2024, 12, 11, 20, 2, 11);
    Item item = new Item()
        .setId(1L)
        .setCreateTime(LocalDateTime.now()).setModVer(1)
        .setName("foo").setDescription("desc");
    Item other = new Item().copyBasicFrom(item);
    assertEquals(1L, other.getId());
    assertEquals(1, other.getModVer());
    assertNotNull(other.getCreateTime());
    assertNull(other.getName());
    assertNull(other.getDescription());

    item.setDeleteTime(deleteTime).setModVer(2);

    other = new Item().copyBasicFrom(item);
    assertEquals(1L, other.getId());
    assertEquals(2, other.getModVer());
    assertNotNull(other.getCreateTime());
    assertNull(other.getName());
    assertNull(other.getDescription());
    assertNotNull(other.getDeleteTime());
  }

  @Test
  void includeBasicPropertyNames() {
    Item item = new Item()
        .setId(1L)
        .setCreateTime(LocalDateTime.now()).setModVer(1)
        .setName("foo").setDescription("desc");

    String[] props = item.includeBasicPropertyNames("name");
    assertArrayEquals(new String[]{PROP_ID, PROP_CREATE_TIME, PROP_MOD_VER, PROP_DELETE_TIME, "name"}, props);
  }
}