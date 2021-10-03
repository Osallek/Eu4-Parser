package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AgeAbility {

    private final ClausewitzItem item;

    public AgeAbility(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Condition getAllow() {
        ClausewitzItem child = item.getChild("allow");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public List<String> getRules() {
        ClausewitzItem child = item.getChild("rule");
        return child == null ? null : child.getVariables().stream().map(ClausewitzVariable::getName).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AgeAbility)) {
            return false;
        }

        AgeAbility ageAbility = (AgeAbility) o;

        return Objects.equals(getName(), ageAbility.getName());
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
