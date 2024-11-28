package wlei.candy.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 实体状态变化的处理器
 * Created by HeLei on 2021/4/15.
 */
public abstract class EntityStateHandler implements Closeable {
  /**
   * 此接口的实现类若添加进此容器中，则会接收到实体变化的消息
   */
  static final CopyOnWriteArrayList<EntityStateHandler> HANDLERS = new CopyOnWriteArrayList<>();
  /**
   * 日志组件
   */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public EntityStateHandler() {
    HANDLERS.add(this);
  }

  /**
   * 保存前
   *
   * @param entity 对应的实体
   */
  protected void prePersist(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("{} prePersist id {}", entity.getClass().getSimpleName(), entity.getId());
    }
  }

  /**
   * 更新前
   *
   * @param entity 对应的实体
   */
  protected void preUpdate(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("{} PreUpdate id {}", entity.getClass().getSimpleName(), entity.getId());
    }
  }

  /**
   * 删除前
   *
   * @param entity 对应的实体
   */
  protected void preRemove(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("entity (name:{}, id:{}) about to be deleted.", entity.getClass().getSimpleName(), entity.getId());
    }
  }

  /**
   * 加载前
   *
   * @param entity 对应的实体
   */
  protected void postLoad(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("{} PostLoad id {}", entity.getClass(), entity.getId());
    }
  }

  /**
   * 保存后
   *
   * @param entity 对应的实体
   */
  protected void postPersist(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("entity (name:{}, id:{}) inserted into database.", entity.getClass().getSimpleName(), entity.getId());
    }
  }

  /**
   * 更新后
   *
   * @param entity 对应的实体
   */
  protected void postUpdate(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("entity (name:{}, id:{}) just updated in the database.", entity.getClass().getSimpleName(), entity.getId());
    }
  }

  /**
   * 删除后
   *
   * @param entity 对应的实体
   */
  protected void postRemove(GenericEntity<?, ?> entity) {
    if (logger.isTraceEnabled()) {
      logger.trace("entity (name:{}, id:{}) about deleted from database.", entity.getClass().getSimpleName(), entity.getId());
    }
  }

  /**
   * 关闭可移除监听
   */
  @Override
  public final void close() {
    HANDLERS.removeIf(handler -> handler == this);
  }

}
