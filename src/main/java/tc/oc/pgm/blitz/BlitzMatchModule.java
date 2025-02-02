package tc.oc.pgm.blitz;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import tc.oc.component.types.PersonalizedText;
import tc.oc.component.types.PersonalizedTranslatable;
import tc.oc.pgm.events.MatchPlayerDeathEvent;
import tc.oc.pgm.events.PlayerParticipationStartEvent;
import tc.oc.pgm.events.PlayerPartyChangeEvent;
import tc.oc.pgm.match.*;
import tc.oc.pgm.spawns.events.ParticipantSpawnEvent;

public class BlitzMatchModule extends MatchModule implements Listener {

  final BlitzConfig config;
  public final LifeManager lifeManager;
  private final Set<UUID> eliminatedPlayers = new HashSet<>();

  public BlitzMatchModule(Match match, BlitzConfig config) {
    super(match);
    this.config = config;
    this.lifeManager = new LifeManager(this.config.getNumLives());
  }

  @Override
  public boolean shouldLoad() {
    return super.shouldLoad() && config.lives != Integer.MAX_VALUE;
  }

  @Override
  public void load() {
    super.load();
    this.getMatch().addVictoryCondition(new BlitzVictoryCondition());
  }

  public BlitzConfig getConfig() {
    return this.config;
  }

  /** Whether or not the player participated in the match and was eliminated. */
  public boolean isPlayerEliminated(UUID player) {
    return this.eliminatedPlayers.contains(player);
  }

  public int getRemainingPlayers(Competitor competitor) {
    // TODO: this becomes a bit more complex when eliminated players are not forced to observers
    return competitor.getPlayers().size();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void handleDeath(final MatchPlayerDeathEvent event) {
    MatchPlayer victim = event.getVictim();
    if (victim.getParty() instanceof Competitor) {
      Competitor competitor = (Competitor) victim.getParty();

      int lives = this.lifeManager.addLives(event.getVictim().getPlayerId(), -1);

      if (lives <= 0) {
        this.handleElimination(victim, competitor);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handleLeave(final PlayerPartyChangeEvent event) {
    int lives = this.lifeManager.getLives(event.getPlayer().getPlayerId());
    if (event.getOldParty() instanceof Competitor && lives > 0) {
      this.handleElimination(event.getPlayer(), (Competitor) event.getOldParty());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void handleJoin(final PlayerParticipationStartEvent event) {
    if (event.getMatch().hasStarted()) {
      event.cancel(
          new PersonalizedTranslatable(
              "blitz.join",
              new PersonalizedText(
                  getMatch().getModuleContext().getGame(), net.md_5.bungee.api.ChatColor.AQUA)));
    }
  }

  @EventHandler
  public void handleSpawn(final ParticipantSpawnEvent event) {
    if (this.config.broadcastLives) {
      int lives = this.lifeManager.getLives(event.getPlayer().getPlayerId());
      event
          .getPlayer()
          .showTitle(
              new PersonalizedText(),
              new PersonalizedText(
                  new PersonalizedTranslatable(
                      "match.blitz.livesRemaining.message",
                      new PersonalizedText(
                          new PersonalizedTranslatable(
                              lives == 1
                                  ? "match.blitz.livesRemaining.singularLives"
                                  : "match.blitz.livesRemaining.pluralLives",
                              new PersonalizedText(Integer.toString(lives))),
                          net.md_5.bungee.api.ChatColor.AQUA)),
                  net.md_5.bungee.api.ChatColor.RED),
              0,
              60,
              20);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onBlitzPlayerEliminated(final BlitzPlayerEliminatedEvent event) {
    this.eliminatedPlayers.add(event.getPlayer().getBukkit().getUniqueId());

    World world = event.getMatch().getWorld();
    Location death = event.getDeathLocation();

    double radius = 0.1;
    int n = 8;
    for (int i = 0; i < 6; i++) {
      double angle = 2 * Math.PI * i / n;
      Location base =
          death.clone().add(new Vector(radius * Math.cos(angle), 0, radius * Math.sin(angle)));
      for (int j = 0; j <= 8; j++) {
        world.playEffect(base, Effect.SMOKE, j);
      }
    }
  }

  private void handleElimination(final MatchPlayer player, Competitor competitor) {
    final BlitzPlayerEliminatedEvent eliminatedEvent =
        new BlitzPlayerEliminatedEvent(
            this.match, player, competitor, player.getBukkit().getLocation());

    // wait until the next tick to do this so stat recording and other stuff works
    getMatch()
        .getScheduler(MatchScope.RUNNING)
        .runTask(
            new Runnable() {
              @Override
              public void run() {
                match.callEvent(eliminatedEvent);
                if (player.getParty() instanceof Competitor) {
                  match.setPlayerParty(player, match.getDefaultParty());
                }
                match.invalidateCompetitorRanking();
                match.checkEnd();
              }
            });
  }
}
