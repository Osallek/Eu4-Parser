package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class TriggeredModifier extends GameModifier {

    public TriggeredModifier(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Modifiers getModifier() {
        return new Modifiers(this.item.getVariables());
    }

    public Condition getPotential() {
        ClausewitzItem child = this.item.getChild("potential");
        return child == null ? null : new Condition(child);
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TriggeredModifier)) {
            return false;
        }

        TriggeredModifier triggeredModifier = (TriggeredModifier) o;

        return Objects.equals(getName(), triggeredModifier.getName());
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
