package wlei.candy.jpa;

import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Bid;
import wlei.candy.jpa.auction.entities.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsualRepositoryImplTest {

  @Test
  void constructor() {
    Fux fux = new Fux();
    assertEquals(Item.class, fux.entityClass);
  }

  private static class Foo<F extends UsualEntity<F>> extends UsualRepositoryImpl<F> {
  }

  private static class Bar<A, R extends UsualEntity<R>> extends Foo<R> {
  }

  private static class Baz<Z> extends Bar<Z, Item> {
  }

  private static class Fux extends Baz<Bid> {
  }
}