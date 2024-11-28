package wlei.candy.jpa;

import jakarta.persistence.*;


/**
 * 实体状态变化的监听器，被GenericEntity使用
 * Author: HeLei
 * Date: 2024/11/25
 */
class EntityStateListener {

  /**
   * 保存前
   *
   * @param entity 对应的实体
   */
  @PrePersist
  void prePersist(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.prePersist(entity);
    }
  }

  /**
   * 更新前
   *
   * @param entity 对应的实体
   */
  @PreUpdate
  void preUpdate(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.preUpdate(entity);
    }
  }

  /**
   * 删除前
   *
   * @param entity 对应的实体
   */
  @PreRemove
  void preRemove(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.preRemove(entity);
    }
  }

  /**
   * 加载前
   *
   * @param entity 对应的实体
   */
  @PostLoad
  void postLoad(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.postLoad(entity);
    }
  }

  /**
   * 保存后
   *
   * @param entity 对应的实体
   */
  @PostPersist
  void postPersist(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.postPersist(entity);
    }
  }

  /**
   * 更新后
   *
   * @param entity 对应的实体
   */
  @PostUpdate
  void postUpdate(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.postUpdate(entity);
    }
  }

  /**
   * 删除后
   *
   * @param entity 对应的实体
   */
  @PostRemove
  void postRemove(GenericEntity<?, ?> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.postRemove(entity);
    }
  }

}
