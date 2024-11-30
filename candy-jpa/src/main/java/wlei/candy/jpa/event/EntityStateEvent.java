package wlei.candy.jpa.event;

import org.springframework.context.ApplicationEvent;
import wlei.candy.jpa.GenericEntity;

import java.io.Serializable;

/**
 * 基础的应用事件
 *
 * @author HeLei
 */
public abstract class EntityStateEvent<I extends Serializable, E extends GenericEntity<I, E>> extends ApplicationEvent {
  /**
   * @param source 事件发生源
   */
  public EntityStateEvent(GenericEntity<I, E> source) {
    super(source);
  }

  @SuppressWarnings("unchecked")
  @Override
  public GenericEntity<I, E> getSource() {
    return (GenericEntity<I, E>) super.getSource();
  }
}
