package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class ModifierDefinition {

    private final String type;

    private final String key;

    private final Condition trigger;

    public ModifierDefinition(ClausewitzItem item) {
        this.type = item.getVarAsString("type");
        this.key = item.getVarAsString("key");
        this.trigger = item.hasChild("trigger") ? new Condition(item.getChild("trigger")) : null;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public Condition getTrigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ModifierDefinition)) {
            return false;
        }

        ModifierDefinition area = (ModifierDefinition) o;

        return Objects.equals(key, area.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }
}
