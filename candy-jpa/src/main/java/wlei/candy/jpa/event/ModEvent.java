package wlei.candy.jpa.event;

import wlei.candy.jpa.GenericEntity;

/**
 * 修改实体事件
 *
 * @author HeLei
 */
public class ModEvent extends EntityStateEvent {

  /**
   * @param source 事件发生源
   */
  public ModEvent(GenericEntity<?, ?> source) {
    super(source);
  }
}