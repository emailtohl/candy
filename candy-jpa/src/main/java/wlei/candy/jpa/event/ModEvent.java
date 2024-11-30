package wlei.candy.jpa.event;

import wlei.candy.jpa.GenericEntity;

import java.io.Serializable;

/**
 * 修改实体事件
 *
 * @author HeLei
 */
public class ModEvent<I extends Serializable, E extends GenericEntity<I, E>> extends EntityStateEvent<I, E> {

  /**
   * @param source 事件发生源
   */
  public ModEvent(GenericEntity<I, E> source) {
    super(source);
  }
}