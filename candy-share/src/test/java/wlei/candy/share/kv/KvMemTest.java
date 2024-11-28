package wlei.candy.share.kv;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by helei on 2023/4/11
 */
class KvMemTest {

  @Test
  void testThreadPoolTaskScheduler() throws InterruptedException {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.initialize();
    KvDB db = new KvMem(threadPoolTaskScheduler::scheduleWithFixedDelay);
    db.set("hello", "world", 0);
    assertEquals("world", db.get("hello").orElseThrow(IllegalStateException::new));
    db.delete("hello");
    assertFalse(db.get("hello").isPresent());
    db.set("hello", "world", 100);
    assertEquals("world", db.get("hello").orElseThrow(IllegalStateException::new));
    TimeUnit.MILLISECONDS.sleep(500L);
    assertFalse(db.get("hello").isPresent());
  }

  @Test
  void testScheduledExecutorService() throws InterruptedException {
    KvDB db = new KvMem();
    db.set("hello", "world", 0);
    assertEquals("world", db.get("hello").orElseThrow(IllegalStateException::new));
    db.delete("hello");
    assertFalse(db.get("hello").isPresent());
    db.set("hello", "world", 100);
    assertEquals("world", db.get("hello").orElseThrow(IllegalStateException::new));
    TimeUnit.MILLISECONDS.sleep(500L);
    assertFalse(db.get("hello").isPresent());
  }

  @Test
  void scheduledFuture() throws ExecutionException, InterruptedException {
    ExpireCleaner cleaner = new ExpireCleanerImpl(Executors.newSingleThreadScheduledExecutor());
    ScheduledFuture<?> scheduledFuture = cleaner.schedule(() -> {
    }, Duration.ofMillis(1));
    if (scheduledFuture.isDone()) {
      System.out.println(scheduledFuture.get());
    }
  }

  @Test
  void h() throws InterruptedException {
    KvDB db = new KvMem();
    db.hset("hello", "foo", "bar", 0);
    String val = db.hget("hello", "foo").orElseThrow(IllegalStateException::new);
    assertEquals("bar", val);
    db.delHash("hello");
    assertFalse(db.hget("hello", "foo").isPresent());

    db.hset("hello", "foo", "bar", 100);
    TimeUnit.MILLISECONDS.sleep(500L);
    assertFalse(db.hget("hello", "foo").isPresent());
  }

}