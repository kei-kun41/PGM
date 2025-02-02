package tc.oc.pgm.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tc.oc.pgm.map.PGMMap;

public class SetNextMapEvent extends Event {

  private final PGMMap map;

  public SetNextMapEvent(PGMMap map) {
    this.map = map;
  }

  public PGMMap getMap() {
    return map;
  }

  private static final HandlerList handlers = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
