package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class ColonialName {

    private final String name;

    private final Condition trigger;

    public ColonialName(ClausewitzItem item) {
        this.name = item.getVarAsString("name");

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

    }

    public String getName() {
        return name;
    }

    public Condition getTrigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ColonialName)) {
            return false;
        }

        ColonialName colonialName = (ColonialName) o;

        return Objects.equals(name, colonialName.name);
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
