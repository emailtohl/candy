package wlei.candy.share.kv;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 基于内存实现
 * <p>
 * Created by helei on 2021/9/26.
 */
public class KvMem implements KvDB {
  private final ConcurrentHashMap<String, String> db = new ConcurrentHashMap<>();
  private final Map<String, ConcurrentHashMap<String, String>> buckets = new HashMap<>();

  private final ExpireCleaner expireCleaner;

  /**
   * 默认使用ScheduledExecutorService执行器来清理
   */
  public KvMem() {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    expireCleaner = new ExpireCleanerImpl(executor);
  }

  /**
   * 一般复用线程执行器，可传入Spring的ThreadPoolTaskScheduler::scheduleWithFixedDelay
   *
   * @param expireCleaner 计划执行器
   */
  public KvMem(ExpireCleaner expireCleaner) {
    this.expireCleaner = expireCleaner;
  }

  @Override
  public void set(String key, String value, long expire) {
    db.put(key, value);
    if (expire > 0) {
      expireCleaner.schedule(() -> db.remove(key), Duration.ofMillis(expire));
    }
  }

  @Override
  public Optional<String> get(String key) {
    String value = db.get(key);
    return Optional.ofNullable(value);
  }

  @Override
  public void delete(String key) {
    db.remove(key);
  }

  @Override
  public void hset(String key, String field, String value, long expire) {
    ConcurrentHashMap<String, String> bucket = getBucket(key);
    bucket.put(field, value);
    if (expire > 0) {
      expireCleaner.schedule(() -> delHash(key), Duration.ofMillis(expire));
    }
  }

  @Override
  public Optional<String> hget(String key, String field) {
    ConcurrentHashMap<String, String> bucket = getBucket(key);
    String value = bucket.get(field);
    return Optional.ofNullable(value);
  }

  @Override
  public void hdel(String key, String field) {
    ConcurrentHashMap<String, String> bucket = getBucket(key);
    bucket.remove(field);
  }

  @Override
  public synchronized void delHash(String key) {
    buckets.remove(key);
  }

  private synchronized ConcurrentHashMap<String, String> getBucket(String bucketName) {
    ConcurrentHashMap<String, String> bucket = buckets.get(bucketName);
    if (bucket != null) {
      return bucket;
    }
    bucket = new ConcurrentHashMap<>();
    buckets.put(bucketName, bucket);
    return bucket;
  }

}
