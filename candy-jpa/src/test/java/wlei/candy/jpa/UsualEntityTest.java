package wlei.candy.jpa;

import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: HeLei
 * Date: 2024/11/30
 */
class UsualEntityTest {

  @Test
  void copyBasicFrom() {
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

    item.setDeleted(true).setModVer(2);

    other = new Item().copyBasicFrom(item);
    assertEquals(1L, other.getId());
    assertEquals(2, other.getModVer());
    assertNotNull(other.getCreateTime());
    assertNull(other.getName());
    assertNull(other.getDescription());
    assertTrue(other.isDeleted());
  }

  @Test
  void includeBasicPropertyNames() {
    Item item = new Item()
        .setId(1L)
        .setCreateTime(LocalDateTime.now()).setModVer(1)
        .setName("foo").setDescription("desc");

    String[] props = item.includeBasicPropertyNames("name");
    assertArrayEquals(new String[]{"id", "createTime", "modVer", "deleted", "name"}, props);
  }
}