package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Modifier {

    private final String name;

    private final ModifierType type;

    private final List<ModifierScope> scopes;

    public Modifier(String name, ModifierType type, ModifierScope... scopes) {
        this.name = ClausewitzUtils.removeQuotes(name.toLowerCase());
        this.type = type;
        this.scopes = Arrays.asList(scopes);
    }

    public String getName() {
        return name;
    }

    public ModifierType getType() {
        return type;
    }

    public List<ModifierScope> getScopes() {
        return scopes;
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

        return Objects.equals(name, area.name);
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
