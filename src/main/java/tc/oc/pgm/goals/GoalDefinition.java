package tc.oc.pgm.goals;

import javax.annotation.Nullable;
import tc.oc.component.Component;
import tc.oc.component.types.PersonalizedText;
import tc.oc.pgm.features.SelfIdentifyingFeatureDefinition;
import tc.oc.pgm.match.Match;

/**
 * Definition of a goal/objective feature. Provides a name field, used to identify the goal to
 * players, and to generate a default ID. There is also a visibility flag. An invisible goal does
 * not appear in any scoreboards, chat messages, or anything else that would directly indicate its
 * existence.
 */
public abstract class GoalDefinition extends SelfIdentifyingFeatureDefinition {
  private final @Nullable Boolean required;
  private final boolean visible;
  private final String name;

  public GoalDefinition(
      @Nullable String id, String name, @Nullable Boolean required, boolean visible) {
    super(id);
    this.name = name;
    this.required = required;
    this.visible = visible;
  }

  @Override
  protected String getDefaultId() {
    return makeDefaultId() + "--" + makeId(this.name);
  }

  public String getName() {
    return this.name;
  }

  public String getColoredName() {
    return this.getName();
  }

  public Component getComponentName() {
    return new PersonalizedText(getName());
  }

  public @Nullable Boolean isRequired() {
    return this.required;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public Goal<? extends GoalDefinition> getGoal(Match match) {
    return (Goal<? extends GoalDefinition>) match.getMatchFeatureContext().get(this.getId());
  }
}
