package wlei.candy.share.kv;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by helei on 2023/4/11
 */
public interface ExpireCleaner {
  /**
   * 延期清理器，一般复用线程执行器，如Spring的ThreadPoolTaskScheduler:
   * ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
   * threadPoolTaskScheduler.initialize();
   * KvDB db = new KvMem(threadPoolTaskScheduler::scheduleWithFixedDelay);
   *
   * @param task  任务
   * @param delay 执行时间
   * @return ScheduledFuture
   */
  ScheduledFuture<?> schedule(Runnable task, Duration delay);
}
