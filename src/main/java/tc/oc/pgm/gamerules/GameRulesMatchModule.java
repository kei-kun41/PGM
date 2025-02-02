package tc.oc.pgm.gamerules;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;

public class GameRulesMatchModule extends MatchModule {

  private final Map<GameRule, Boolean> gameRules;

  public GameRulesMatchModule(Match match, Map<GameRule, Boolean> gameRules) {
    super(match);
    this.gameRules = Preconditions.checkNotNull(gameRules, "gamerules");
  }

  @Override
  public void load() {
    for (Map.Entry<GameRule, Boolean> gameRule : this.gameRules.entrySet()) {
      this.match
          .getWorld()
          .setGameRuleValue(gameRule.getKey().getValue(), gameRule.getValue().toString());
    }
  }

  public ImmutableMap<GameRule, Boolean> getGameRules() {
    return ImmutableMap.copyOf(gameRules);
  }
}
