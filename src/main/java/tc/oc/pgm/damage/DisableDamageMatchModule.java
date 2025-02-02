package tc.oc.pgm.damage;

import com.google.common.collect.SetMultimap;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.*;
import tc.oc.pgm.tracker.TrackerMatchModule;
import tc.oc.pgm.tracker.damage.DamageInfo;

@ListenerScope(MatchScope.RUNNING)
public class DisableDamageMatchModule extends MatchModule implements Listener {

  protected final SetMultimap<DamageCause, PlayerRelation> causes;

  public DisableDamageMatchModule(Match match, SetMultimap<DamageCause, PlayerRelation> causes) {
    super(match);
    this.causes = causes;
  }

  private static DamageCause getBlockDamageCause(Block block) {
    switch (block.getType()) {
      case LAVA:
      case STATIONARY_LAVA:
        return DamageCause.LAVA;

      case FIRE:
        return DamageCause.FIRE;

      default:
        return DamageCause.CONTACT;
    }
  }

  private boolean canDamage(
      DamageCause cause, MatchPlayer victim, @Nullable ParticipantState damager) {
    return !this.causes.containsEntry(
        cause, PlayerRelation.get(victim.getParticipantState(), damager));
  }

  private boolean canDamage(DamageCause cause, MatchPlayer victim, DamageInfo info) {
    return !this.causes.containsEntry(
        cause, PlayerRelation.get(victim.getParticipantState(), info.getAttacker()));
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void handleIgnition(EntityCombustByBlockEvent event) {
    MatchPlayer victim = getMatch().getParticipant(event.getEntity());
    if (victim == null) return;

    ParticipantState attacker =
        match.needMatchModule(TrackerMatchModule.class).getOwner(event.getCombuster());

    // Disabling FIRE/LAVA damage also prevents setting on fire
    if (!this.canDamage(getBlockDamageCause(event.getCombuster()), victim, attacker)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void handleDamage(EntityDamageEvent event) {
    MatchPlayer victim = getMatch().getParticipant(event.getEntity());
    if (victim == null) return;

    DamageInfo damageInfo = match.needMatchModule(TrackerMatchModule.class).resolveDamage(event);
    if (!canDamage(event.getCause(), victim, damageInfo)) {
      event.setCancelled(true);
    }
  }
}
