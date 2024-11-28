package wlei.candy.jpa.event;

import org.springframework.context.ApplicationEvent;
import wlei.candy.jpa.GenericEntity;

/**
 * 基础的应用事件
 *
 * @author HeLei
 */
public abstract class EntityStateEvent extends ApplicationEvent {
  /**
   * @param source 事件发生源
   */
  public EntityStateEvent(GenericEntity<?, ?> source) {
    super(source);
  }

  @Override
  public GenericEntity<?, ?> getSource() {
    return (GenericEntity<?, ?>) super.getSource();
  }
}
