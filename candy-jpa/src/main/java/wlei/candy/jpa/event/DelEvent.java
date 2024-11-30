package wlei.candy.jpa.event;

import wlei.candy.jpa.GenericEntity;

/**
 * 删除实体事件
 *
 * @author HeLei
 */
public class DelEvent extends EntityStateEvent {

  /**
   * @param source 事件发生源
   */
  public DelEvent(GenericEntity<?, ?> source) {
    super(source);
  }
}