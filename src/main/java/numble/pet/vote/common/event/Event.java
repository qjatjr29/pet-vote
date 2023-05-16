package numble.pet.vote.common.event;

public abstract class Event {

  private Long timestamp;

  public Event() {
    this.timestamp = System.currentTimeMillis();
  }

  public Long getTimestamp() {
    return timestamp;
  }
}
