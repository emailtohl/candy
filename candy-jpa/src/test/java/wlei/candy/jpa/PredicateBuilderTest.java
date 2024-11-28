package wlei.candy.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wlei.candy.jpa.auction.entities.Bid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PredicateBuilderTest {
  CriteriaBuilder cb;
  Root<Bid> r;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    cb = mock(CriteriaBuilder.class);
    r = mock(Root.class);
    when(r.get(anyString())).thenReturn(mock(Path.class));
  }

  @Test
  void getPredicates() {
    QueryParameters qp = new QueryParameters()
        .add("p1", 1)
        .add("p2", " hello ")
        .add("p2.p3", "world%")
        .add("p4", LocalDateTime.now())
        .add("p5", new char[]{'a', 'b'})
        .setSupplement((bb, rr) -> new ArrayList<>());
    PredicateBuilder<Long, Bid> b = new PredicateBuilder<>(cb, r);
    List<Predicate> p = b.getPredicates(qp);
    assertEquals(5, p.size());
  }
}