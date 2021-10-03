package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;

import java.util.Objects;

public class Modifier {

    private final String name;

    private final ModifierType type;

    private final ModifierScope scope;

    public Modifier(String name, ModifierType type, ModifierScope scope) {
        this.name = ClausewitzUtils.removeQuotes(name.toLowerCase());
        this.type = type;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public ModifierType getType() {
        return type;
    }

    public ModifierScope getScope() {
        return scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Modifier)) {
            return false;
        }

        Modifier area = (Modifier) o;

        return Objects.equals(getName(), area.getName());
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
