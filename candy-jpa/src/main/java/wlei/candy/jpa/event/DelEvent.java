package wlei.candy.jpa.event;

import wlei.candy.jpa.GenericEntity;

import java.io.Serializable;

/**
 * 删除实体事件
 *
 * @author HeLei
 */
public class DelEvent<I extends Serializable, E extends GenericEntity<I, E>> extends EntityStateEvent<I, E> {

  /**
   * @param source 事件发生源
   */
  public DelEvent(GenericEntity<I, E> source) {
    super(source);
  }
}