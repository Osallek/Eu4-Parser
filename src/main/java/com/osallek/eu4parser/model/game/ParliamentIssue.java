package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParliamentIssue {

    private final String name;

    private String localizedName;

    private final Power category;

    private final Condition allow;

    private final Map<String, List<String>> modifiers;

    public ParliamentIssue(ClausewitzItem item) {
        this.name = item.getName();
        this.category = Power.byParliamentType(item.getVarAsInt("category"));

        ClausewitzItem child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                                                    Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        child = item.getChild("allow");
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

    public Power getCategory() {
        return category;
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

        ParliamentIssue that = (ParliamentIssue) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
