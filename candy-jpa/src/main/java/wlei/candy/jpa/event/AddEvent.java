package wlei.candy.jpa.event;

import wlei.candy.jpa.GenericEntity;

/**
 * 创建实体事件
 *
 * @author HeLei
 */
public class AddEvent extends EntityStateEvent {

  /**
   * @param source 事件发生源
   */
  public AddEvent(GenericEntity<?, ?> source) {
    super(source);
  }
}
