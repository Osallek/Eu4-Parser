package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AgeAbility {

    private final String name;

    private String localizedName;

    private final Condition allow;

    private final Modifiers modifiers;

    private final List<String> rules;

    public AgeAbility(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzItem child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);

        this.modifiers = new Modifiers(item.getChild("modifier"));

        child = item.getChild("rule");
        this.rules = child == null ? null : child.getVariables().stream().map(ClausewitzVariable::getName).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public List<String> getRules() {
        return rules;
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

        return Objects.equals(name, ageAbility.name);
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
