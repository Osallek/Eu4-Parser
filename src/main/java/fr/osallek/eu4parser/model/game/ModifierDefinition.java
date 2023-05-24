package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;

public class ModifierDefinition {

    private final ClausewitzItem item;

    public ModifierDefinition(ClausewitzItem item) {
        this.item = item;
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        this.item.setVariable("type", type);
    }

    public String getKey() {
        return this.item.getVarAsString("key");
    }

    public void setKey(String key) {
        this.item.setVariable("key", key);
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ModifierDefinition modifierDefinition)) {
            return false;
        }

        return Objects.equals(getKey(), modifierDefinition.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public String toString() {
        return getKey();
    }
}
