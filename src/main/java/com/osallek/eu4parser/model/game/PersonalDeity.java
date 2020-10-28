package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PersonalDeity {

    private final String name;

    private String localizedName;

    private final int sprite;

    private final Condition allow;

    private final Map<String, List<String>> modifiers;

    public PersonalDeity(ClausewitzItem item) {
        this.name = item.getName();
        this.sprite = item.getVarAsInt("sprite");

        this.modifiers = item.getVarsNot("sprite")
                             .stream()
                             .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                            Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        this.allow = item.getChild("allow") == null ? null : new Condition(item.getChild("allow"));
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public int getSprite() {
        return sprite;
    }

    public Condition getAllow() {
        return allow;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PersonalDeity)) {
            return false;
        }

        PersonalDeity area = (PersonalDeity) o;

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
