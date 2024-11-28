package wlei.candy.share.kv;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by helei on 2023/4/11
 */
class ExpireCleanerImpl implements ExpireCleaner {
  private final ScheduledExecutorService executor;

  public ExpireCleanerImpl(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable task, Duration delay) {
    return executor.schedule(task, delay.toMillis(), TimeUnit.MILLISECONDS);
  }
}
