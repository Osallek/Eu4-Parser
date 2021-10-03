package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Age {

    private final ClausewitzItem item;

    public Age(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getStart() {
        return this.item.getVarAsInt("start");
    }

    public void setStart(int start) {
        this.item.setVariable("start", start);
    }

    public Boolean getReligiousConflict() {
        return this.item.getVarAsBool("religious_conflicts");
    }

    public void setReligiousConflict(Boolean religiousConflict) {
        if (religiousConflict == null) {
            this.item.removeVariable("religious_conflicts");
        } else {
            this.item.setVariable("religious_conflicts", religiousConflict);
        }
    }

    public Condition getCanStart() {
        ClausewitzItem child = item.getChild("can_start");
        return child == null ? null : new Condition(child);
    }

    public Double getPapacy() {
        return this.item.getVarAsDouble("papacy");
    }

    public void setPapacy(Double papacy) {
        if (papacy == null) {
            this.item.removeVariable("papacy");
        } else {
            this.item.setVariable("papacy", papacy);
        }
    }

    public Map<String, Integer> getAbsolutism() {
        ClausewitzItem child = this.item.getChild("absolutism");
        return child == null ? null : child.getVariables()
                                           .stream()
                                           .collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getAsInt));
    }

    public void setAbsolutism(Map<String, Integer> absolutism) {
        if (MapUtils.isEmpty(absolutism)) {
            this.item.removeChild("absolutism");
        } else {
            ClausewitzItem child = this.item.getChild("absolutism");

            if (child == null) {
                child = this.item.addChild("absolutism");
            } else {
                child.removeAllVariables();
            }

            ClausewitzItem finalChild = child;
            absolutism.forEach((s, integer) -> {
                if (integer != null) {
                    finalChild.addVariable(s, integer);
                }
            });
        }
    }

    public Map<String, AgeObjective> getObjectives() {
        ClausewitzItem child = this.item.getChild("objectives");
        return child == null ? null : child.getChildren()
                                           .stream()
                                           .map(AgeObjective::new)
                                           .collect(Collectors.toMap(AgeObjective::getName, Function.identity()));
    }

    public Map<String, AgeAbility> getAbilities() {
        ClausewitzItem child = item.getChild("abilities");
        return child == null ? null : child.getChildren()
                                           .stream()
                                           .map(AgeAbility::new)
                                           .collect(Collectors.toMap(AgeAbility::getName, Function.identity()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Age)) {
            return false;
        }

        Age age = (Age) o;

        return Objects.equals(getName(), age.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
