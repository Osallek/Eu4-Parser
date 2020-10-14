package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.time.MonthDay;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RulerPersonality {
    private final String name;

    private String localizedName;

    private Condition ruler_allow;
    private Condition heir_allow;
    private Condition consort_allow;
    private Condition allow;

    private final Map<String, List<String>> modifiers;

    public RulerPersonality(ClausewitzItem item) {
        this.name = item.getName();

        this.allow = item.getChild("allow") == null ? null : new Condition(item.getChild("allow"));
        this.ruler_allow = item.getChild("ruler_allow") == null ? null : new Condition(item.getChild("ruler_allow"));
        this.heir_allow = item.getChild("heir_allow") == null ? null : new Condition(item.getChild("heir_allow"));
        this.consort_allow = item.getChild("consort_allow") == null ? null : new Condition(item.getChild("consort_allow"));

        List<ClausewitzVariable> list = item.getVarsNot();
        this.modifiers = list.stream()
                             .collect(Collectors.groupingBy(ClausewitzObject::getName, Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));


    }

    public Condition getRuler_allow() {
        return ruler_allow;
    }

    public Condition getHeir_allow() {
        return heir_allow;
    }

    public Condition getConsort_allow() {
        return consort_allow;
    }

    public Condition getAllow() {
        return allow;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RulerPersonality)) {
            return false;
        }

        RulerPersonality personality = (RulerPersonality) o;

        return Objects.equals(name, personality.name);
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
