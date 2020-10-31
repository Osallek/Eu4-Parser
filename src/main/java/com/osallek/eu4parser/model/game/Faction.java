package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Faction {

    private final String name;

    private String localizedName;

    private final Power category;

    private final Condition trigger;

    private final List<Names> names;

    private final Modifiers modifiers;

    public Faction(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzVariable var = item.getVar("monarch_power");
        this.category = var == null ? null : Power.valueOf(var.getValue().toUpperCase());

        this.modifiers = new Modifiers(item.getChild("modifier"));

        List<ClausewitzItem> namesItems = item.getChildren("triggered_faction_name");
        this.names = namesItems.stream().map(Names::new).collect(Collectors.toList());

        item.removeVariable("monarch_power");
        item.removeChild("modifier");
        item.removeChild("triggered_faction_name");

        this.trigger = new Condition(item);
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

    public Condition getTrigger() {
        return trigger;
    }

    public List<Names> getNames() {
        return names;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Faction)) {
            return false;
        }

        Faction area = (Faction) o;

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
