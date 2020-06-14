package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Decree {

    private final ClausewitzItem item;

    private String localizedName;

    public Decree(ClausewitzItem item) {
        this.item = item;
    }

    public Decree(Decree other) {
        this.item = other.item;
        this.localizedName = other.localizedName;
    }

    public Decree(String name) {
        this.item = new ClausewitzItem(null, name, 0);
    }

    public String getName() {
        return this.item.getName();
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Integer getCost() {
        return this.item.getVarAsInt("cost");
    }

    public void setCost(int cost) {
        this.item.setVariable("cost", cost);
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public void setDuration(int duration) {
        this.item.setVariable("duration", duration);
    }


    public Map<String, Double> getModifiers() {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            return modifiersItem.getVariables()
                                .stream()
                                .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                          ClausewitzVariable::getAsDouble,
                                                          (a, b) -> b,
                                                          LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addModifier(String modifier, Double quantity) {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            modifiersItem.setVariable(modifier, quantity);
        }
    }

    public void removeModifier(String modifier) {
        ClausewitzItem modifiersItem = this.item.getChild("modifier");

        if (modifiersItem != null) {
            modifiersItem.removeVariable(modifier);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null && getName() == null) {
            return true;
        }

        if (!(o instanceof Decree)) {
            return false;
        }
        Decree decree = (Decree) o;
        return Objects.equals(getName(), decree.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
