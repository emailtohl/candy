package wlei.candy.jpa;

import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Author: HeLei
 * Date: 2024/11/30
 */
class GenericEntityTest {

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