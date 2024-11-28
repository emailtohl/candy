package wlei.candy.jpa.cache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import wlei.candy.jpa.GenericEntity;
import wlei.candy.jpa.UsualEntity;
import wlei.candy.jpa.event.DelEvent;
import wlei.candy.jpa.event.EntityStateEvent;
import wlei.candy.jpa.event.ModEvent;

import java.util.Optional;

/**
 * Created by HeLei on 2021/4/20.
 */
@Service
class CacheServiceImpl implements CacheService, ApplicationListener<EntityStateEvent> {
  @PersistenceContext
  EntityManager entityManager;
  @Autowired
  CacheManager cacheManager;

  @Override
  public <T extends UsualEntity<T>> T get(Class<T> clz, long id) {
    return entityManager.find(clz, id);
  }

  @Override
  public void onApplicationEvent(@NonNull EntityStateEvent event) {
    if (event instanceof ModEvent || event instanceof DelEvent) {
      Cache cache = cacheManager.getCache(CACHE_NAME);
      if (cache != null) {
        Optional.ofNullable(event.getSource()).map(GenericEntity::getId).ifPresent(cache::evict);
      }
    }
  }
}
