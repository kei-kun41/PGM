package tc.oc.pgm.match;

public class Observers extends ObservingParty {

  public Observers(Match match) {
    super(match);
  }

  @Override
  public String getDefaultName() {
    return "Observers";
  }

  @Override
  public org.bukkit.ChatColor getColor() {
    return org.bukkit.ChatColor.AQUA;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{match=" + getMatch() + "}";
  }
}
