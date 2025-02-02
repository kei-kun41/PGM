package tc.oc.pgm.commands.provider;

import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.bukkit.parametric.provider.BukkitProvider;
import java.lang.annotation.Annotation;
import java.util.List;
import org.bukkit.command.CommandSender;
import tc.oc.chat.Audience;
import tc.oc.chat.BukkitAudiences;

public class AudienceProvider implements BukkitProvider<Audience> {

  @Override
  public boolean isProvided() {
    return true;
  }

  @Override
  public Audience get(CommandSender sender, CommandArgs args, List<? extends Annotation> list) {
    return BukkitAudiences.getAudience(sender);
  }
}
