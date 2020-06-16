package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GoldenBull {

    private final ClausewitzItem item;

    private String localizedName;

    public GoldenBull(ClausewitzItem item) {
        this.item = item;
    }

    public GoldenBull(GoldenBull other) {
        this.item = other.item;
        this.localizedName = other.localizedName;
    }

    public GoldenBull(String name) {
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

    public List<String> getMechanics() {
        ClausewitzList list = this.item.getList("mechanics");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public void setMechanics(List<String> mechanics) {
        ClausewitzList list = this.item.getList("mechanics");

        if (mechanics == null) {
            list = this.item.addList("mechanics", mechanics);
        } else {
            list.clear();
            list.addAll(mechanics);
        }
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

        if (!(o instanceof GoldenBull)) {
            return false;
        }
        GoldenBull decree = (GoldenBull) o;
        return Objects.equals(getName(), decree.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
