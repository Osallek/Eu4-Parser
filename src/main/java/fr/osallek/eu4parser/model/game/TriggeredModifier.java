package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class TriggeredModifier extends GameModifier {

    private final Condition potential;

    private final Condition trigger;

    public TriggeredModifier(ClausewitzItem item) {
        super(item, new Modifiers(item.getVariables()));
        this.potential = item.getChild("potential") == null ? null : new Condition(item.getChild("potential"));
        this.trigger = item.getChild("trigger") == null ? null : new Condition(item.getChild("trigger"));
    }

    public Condition getPotential() {
        return potential;
    }

    public Condition getTrigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TriggeredModifier)) {
            return false;
        }

        TriggeredModifier that = (TriggeredModifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
