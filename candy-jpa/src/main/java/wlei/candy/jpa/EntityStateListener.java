package wlei.candy.jpa;

import jakarta.persistence.*;

import java.io.Serializable;


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
  <I extends Serializable, E extends GenericEntity<I, E>> void prePersist(GenericEntity<I, E> entity) {
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
  <I extends Serializable, E extends GenericEntity<I, E>> void preUpdate(GenericEntity<I, E> entity) {
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
  <I extends Serializable, E extends GenericEntity<I, E>> void preRemove(GenericEntity<I, E> entity) {
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
  <I extends Serializable, E extends GenericEntity<I, E>> void postLoad(GenericEntity<I, E> entity) {
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
  <I extends Serializable, E extends GenericEntity<I, E>> void postPersist(GenericEntity<I, E> entity) {
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
  <I extends Serializable, E extends GenericEntity<I, E>> void postUpdate(GenericEntity<I, E> entity) {
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
  <I extends Serializable, E extends GenericEntity<I, E>> void postRemove(GenericEntity<I, E> entity) {
    for (EntityStateHandler handler : EntityStateHandler.HANDLERS) {
      handler.postRemove(entity);
    }
  }

}
