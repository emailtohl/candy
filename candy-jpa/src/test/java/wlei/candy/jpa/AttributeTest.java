package wlei.candy.jpa;

import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.Item;
import wlei.candy.jpa.auction.entities.Participator;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {
  private Item item;
  private Set<Attribute> itemAttributes;

  @BeforeEach
  void setUp() {
    item = new Item();
    item.setId(110L);
    item.setName("foo");
    item.getBids().add(new Bid("bar", item, new Participator("baz"), new BigDecimal(100)));
    itemAttributes = AttributeFactory.parse(item.getClass());
  }

  @Test
  void getValue() {
    Attribute attribute = itemAttributes.stream().filter(a -> "name".equals(a.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
    String name = attribute.getValue(item, String.class);
    assertEquals("foo", name);

    attribute = itemAttributes.stream().filter(a -> "id".equals(a.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
    long id = attribute.getValue(item, Long.class);
    assertEquals(110, id);
  }

  @Test
  void getGenericClass() {
    Attribute attribute = itemAttributes.stream().filter(a -> "bids".equals(a.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
    Class<?>[] genericClass = attribute.getGenericClass();
    assertEquals(1, genericClass.length);
    assertEquals(Bid.class, genericClass[0]);
  }

  @Test
  void toGenericString() {
    Attribute attribute = itemAttributes.stream().filter(a -> "bids".equals(a.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
    String genericString = attribute.toGenericString();
    assertTrue(genericString.contains(".Bid"));
  }

  @Test
  void getAnnotation() {
    Attribute attribute = itemAttributes.stream().filter(a -> "bids".equals(a.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
    OneToMany oneToMany = attribute.getAnnotation(OneToMany.class);
    assertNotNull(oneToMany);
  }

  @Test
  void testEquals() {
    Item otherItem = new Item();
    otherItem.setId(110L);
    assertEquals(item, otherItem);
    assertEquals(item.hashCode(), otherItem.hashCode());
  }
}