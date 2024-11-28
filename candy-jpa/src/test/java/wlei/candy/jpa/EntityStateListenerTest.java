package wlei.candy.jpa;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wlei.candy.jpa.auction.entities.Bid;

import static org.junit.jupiter.api.Assertions.assertFalse;

class EntityStateListenerTest {
  private Level level;
  private SomeEntityStateHandler someEntityStateHandler;

  @BeforeEach
  void setUp() {
    Logger logger = LoggerFactory.getLogger(SomeEntityStateHandler.class);
    if (logger instanceof ch.qos.logback.classic.Logger) {
      ch.qos.logback.classic.Logger logback = (ch.qos.logback.classic.Logger) logger;
      level = logback.getLevel();
      logback.setLevel(Level.TRACE);
    }
    someEntityStateHandler = new SomeEntityStateHandler();
  }

  @AfterEach
  void recover() {
    Logger logger = LoggerFactory.getLogger(SomeEntityStateHandler.class);
    if (logger instanceof ch.qos.logback.classic.Logger) {
      ch.qos.logback.classic.Logger logback = (ch.qos.logback.classic.Logger) logger;
      logback.setLevel(level);
    }
    someEntityStateHandler.close();
  }

  @Test
  void prePersist() {
    EntityStateListener l = new EntityStateListener();
    l.prePersist(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  @Test
  void preUpdate() {
    EntityStateListener l = new EntityStateListener();
    l.preUpdate(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  @Test
  void preRemove() {
    EntityStateListener l = new EntityStateListener();
    l.preRemove(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  @Test
  void postLoad() {
    EntityStateListener l = new EntityStateListener();
    l.postLoad(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  @Test
  void postPersist() {
    EntityStateListener l = new EntityStateListener();
    l.postPersist(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  @Test
  void postUpdate() {
    EntityStateListener l = new EntityStateListener();
    l.postUpdate(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  @Test
  void postRemove() {
    EntityStateListener l = new EntityStateListener();
    l.postRemove(new Bid());
    assertFalse(EntityStateHandler.HANDLERS.isEmpty());
  }

  private static class SomeEntityStateHandler extends EntityStateHandler {
  }
}