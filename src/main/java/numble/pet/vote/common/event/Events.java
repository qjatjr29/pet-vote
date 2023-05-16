package numble.pet.vote.common.event;

import static java.util.Objects.isNull;

import org.springframework.context.ApplicationEventPublisher;

public class Events {

  private static ApplicationEventPublisher publisher;

  public static void setPublisher(ApplicationEventPublisher publisher) {
    Events.publisher = publisher;
  }

  public static void raise(Object event) {
    if(!isNull(publisher)) publisher.publishEvent(event);
  }

}
