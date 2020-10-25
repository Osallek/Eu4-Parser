package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NavalDoctrine {

    private final String name;

    private String localizedName;

    private final Integer buttonGfx;

    private final Condition allow;

    private final Map<String, List<String>> modifiers;

    public NavalDoctrine(ClausewitzItem item) {
        this.name = item.getName();
        this.buttonGfx = item.getVarAsInt("button_gfx");

        ClausewitzItem child = item.getChild("country_modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                                                    Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        child = item.getChild("can_select");
        this.allow = child == null ? null : new Condition(child);
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

    public Integer getButtonGfx() {
        return buttonGfx;
    }

    public Condition getAllow() {
        return allow;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NavalDoctrine that = (NavalDoctrine) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
