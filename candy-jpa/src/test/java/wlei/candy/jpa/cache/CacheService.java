package wlei.candy.jpa.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wlei.candy.jpa.UsualEntity;

/**
 * Created by HeLei on 2021/4/20.
 */
@Service
public interface CacheService {
  String CACHE_NAME = "CacheService";

  @Transactional
  @Cacheable(value = CACHE_NAME, key = "#root.args[1]")
  <T extends UsualEntity<T>> T get(Class<T> clz, long id);

}
