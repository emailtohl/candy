package wlei.candy.jpa.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import wlei.candy.jpa.EntityStateHandler;
import wlei.candy.jpa.GenericEntity;

import java.io.Serializable;

/**
 * 当实体发生变化后，将消息通知到spring的上下文中，需注册为Spring的bean
 * <p>
 * Created by HeLei on 2021/4/15.
 */
public class EntityStateEventPublisher extends EntityStateHandler implements ApplicationListener<ContextClosedEvent> {
  private final ApplicationEventPublisher publisher;

  public EntityStateEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  protected <I extends Serializable, E extends GenericEntity<I, E>> void postPersist(GenericEntity<I, E> entity) {
    publisher.publishEvent(new AddEvent<>(entity));
  }

  @Override
  protected <I extends Serializable, E extends GenericEntity<I, E>> void postUpdate(GenericEntity<I, E> entity) {
    publisher.publishEvent(new ModEvent<>(entity));
  }

  @Override
  protected <I extends Serializable, E extends GenericEntity<I, E>> void postRemove(GenericEntity<I, E> entity) {
    publisher.publishEvent(new DelEvent<>(entity));
  }

  @Override
  public void onApplicationEvent(@NonNull ContextClosedEvent event) {
    super.close();
  }
}
