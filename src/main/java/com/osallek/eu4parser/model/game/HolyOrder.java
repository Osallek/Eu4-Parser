package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HolyOrder {

    private final String name;

    private String localizedName;

    private final String icon;

    private final Power category;

    private final Condition trigger;

    private final Map<String, List<String>> modifiers;

    public HolyOrder(ClausewitzItem item) {
        this.name = item.getName();
        this.icon = item.getVarAsString("icon");
        this.category = Power.valueOf(item.getVarAsString("cost_type").replace("_power", "").toUpperCase());

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

        child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                                                    Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
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

    public String getIcon() {
        return icon;
    }

    public Power getCategory() {
        return category;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HolyOrder)) {
            return false;
        }

        HolyOrder area = (HolyOrder) o;

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
