package wlei.candy.jpa;

import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Item;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AttributeFactoryTest {

  @Test
  void parse() {
    Item i = new Item();
    i.setName("foo");
    Set<Attribute> attributes = AttributeFactory.parse(i.getClass());
    Attribute attribute = attributes.stream().filter(a -> "name".equals(a.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
    assertEquals("foo", attribute.getValue(i));
  }

  @Test
  void parseFields() {
    Item i = new Item();
    i.setName("foo");
    Class<?>[] bound = AttributeFactory.findEntityBound(i.getClass());
    Set<Attribute> properties = AttributeFactory.parseProperties(bound[0], bound[1]);
    for (Attribute a : properties) {
      if ("name".equals(a.getName())) {
        Object value = a.getValue(i);
        assertEquals("foo", value);
      }
    }
  }

  @Test
  void parseProperties() {
    Item c = new Item();
    c.setName("foo");
    Class<?>[] bound = AttributeFactory.findEntityBound(c.getClass());
    Set<Attribute> properties = AttributeFactory.parseFields(bound[0], bound[1]);
    for (Attribute a : properties) {
      if ("name".equals(a.getName())) {
        Object value = a.getValue(c);
        assertEquals("foo", value);
      }
    }
  }

  @Test
  void getAccessType() {
    AccessType accessType = AttributeFactory.getAccessType(Item.class);
    assertEquals(AccessType.FIELD, accessType);
    accessType = AttributeFactory.getAccessType(TempRecord.class);
    assertEquals(AccessType.PROPERTY, accessType);
    assertThrows(IllegalArgumentException.class, () -> AttributeFactory.getAccessType(AttributeFactory.class));
  }

  @Test
  void findAccessTypeByClass() {
    Optional<AccessType> o = AttributeFactory.findAccessTypeByClass(Item.class);
    assertTrue(o.isPresent());
    assertEquals(AccessType.FIELD, o.get());
  }

  @Test
  void findAccessTypeByField() {
    Optional<AccessType> o = AttributeFactory.findAccessTypeByField(Item.class);
    assertTrue(o.isPresent());
    assertEquals(AccessType.FIELD, o.get());
    o = AttributeFactory.findAccessTypeByField(TempRecord.class);
    assertFalse(o.isPresent());
  }

  @Test
  void findAccessTypeByProperty() {
    Optional<AccessType> o = AttributeFactory.findAccessTypeByProperty(TempRecord.class);
    assertTrue(o.isPresent());
    assertEquals(AccessType.PROPERTY, o.get());
    o = AttributeFactory.findAccessTypeByProperty(Item.class);
    assertFalse(o.isPresent());
  }

  @Test
  void findEntityBound() {
    Class<?>[] bound = AttributeFactory.findEntityBound(Item.class);
    assertEquals(2, bound.length);
    assertEquals(Item.class, bound[0]);
    assertEquals(GenericEntity.class.getSuperclass(), bound[1]);
  }

  @Test
  void findAccessTypeByIdAnnotation() {
    Optional<AccessType> o = AttributeFactory.findAccessTypeByProperty(TempRecord.class);
    assertTrue(o.isPresent());
    assertEquals(AccessType.PROPERTY, o.get());
  }

  /**
   * Author: HeLei
   * Date: 2024/11/28
   */
  public static class TempRecord extends BeanEntity<TempRecord> {
    private int recordType;

    @Column(nullable = false)
    public int getRecordType() {
      return recordType;
    }

    public TempRecord setRecordType(int recordType) {
      this.recordType = recordType;
      return this;
    }
  }
}