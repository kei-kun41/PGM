package tc.oc.pgm.start;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.joda.time.Duration;
import tc.oc.component.Component;
import tc.oc.component.types.PersonalizedText;
import tc.oc.component.types.PersonalizedTranslatable;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.teams.Team;
import tc.oc.util.components.PeriodFormats;

/** Optional countdown between teams being finalized and match starting */
public class HuddleCountdown extends PreMatchCountdown implements Listener {

  public HuddleCountdown(Match match) {
    super(match);
  }

  @Override
  protected Component formatText() {
    return new PersonalizedText(
        new PersonalizedTranslatable(
            "countdown.huddle.message", secondsRemaining(ChatColor.DARK_RED)),
        ChatColor.YELLOW);
  }

  @Override
  public void onStart(Duration remaining, Duration total) {
    super.onStart(remaining, total);

    getMatch().registerEvents(this);

    for (Competitor competitor : getMatch().getCompetitors()) {
      if (competitor instanceof Team) {
        competitor.sendMessage(
            new PersonalizedText(
                new PersonalizedTranslatable(
                    "huddle.instructions", PeriodFormats.briefNaturalPrecise(total)),
                ChatColor.YELLOW));
      }
    }
  }

  protected void cleanup() {
    HandlerList.unregisterAll(this);
  }

  @Override
  public void onEnd(Duration total) {
    super.onEnd(total);
    cleanup();
    getMatch().start();
  }

  @Override
  public void onCancel(Duration remaining, Duration total) {
    super.onCancel(remaining, total);
    cleanup();
  }
}
