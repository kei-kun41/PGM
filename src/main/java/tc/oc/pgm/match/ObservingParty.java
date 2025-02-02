package tc.oc.pgm.match;

import javax.annotation.Nullable;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import tc.oc.component.Component;
import tc.oc.component.types.PersonalizedText;
import tc.oc.named.NameStyle;
import tc.oc.server.BukkitUtils;
import tc.oc.util.components.ComponentUtils;

public abstract class ObservingParty extends MultiPlayerParty {

  private String coloredName;
  private Component componentName;
  private Component chatPrefix;

  public ObservingParty(Match match) {
    super(match);
  }

  @Override
  public String getName() {
    return getDefaultName();
  }

  @Override
  public String getName(@Nullable CommandSender viewer) {
    return getName();
  }

  @Override
  public boolean isNamePlural() {
    return true;
  }

  public net.md_5.bungee.api.ChatColor getBungeeColor() {
    return ComponentUtils.convert(getColor());
  }

  @Override
  public Color getFullColor() {
    return BukkitUtils.colorOf(this.getColor());
  }

  @Override
  public String getColoredName() {
    if (coloredName == null) {
      coloredName = getColor() + getName();
    }
    return coloredName;
  }

  @Override
  public String getColoredName(@Nullable CommandSender viewer) {
    return getColoredName();
  }

  @Override
  public Component getComponentName() {
    if (componentName == null) {
      componentName = new PersonalizedText(getName(), getBungeeColor());
    }
    return componentName;
  }

  @Override
  public Component getStyledName(NameStyle style) {
    return getComponentName();
  }

  @Override
  public Component getChatPrefix() {
    if (chatPrefix == null) {
      chatPrefix = new PersonalizedText("(Obs) ", getBungeeColor());
    }
    return chatPrefix;
  }

  @Override
  public boolean isAutomatic() {
    return false;
  }

  @Override
  public Type getType() {
    return Type.Observing;
  }

  @Override
  public boolean isParticipatingType() {
    return false;
  }

  @Override
  public boolean isParticipating() {
    return false;
  }

  @Override
  public boolean isObservingType() {
    return true;
  }

  @Override
  public boolean isObserving() {
    return true;
  }
}
