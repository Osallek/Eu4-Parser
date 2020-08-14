package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PapacyConcession {

    private String name;

    private String harshLocalizedName;

    private String concilatoryLocalizedName;

    private Map<String, Double> harshModifiers;

    private Map<String, Double> concilatoryModifiers;

    public PapacyConcession(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzItem child = item.getChild("harsh");
        this.harshModifiers = child == null ? null : child.getVariables()
                                                          .stream()
                                                          .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                    ClausewitzVariable::getAsDouble,
                                                                                    (a, b) -> b,
                                                                                    LinkedHashMap::new));
        child = item.getChild("concilatory");
        this.concilatoryModifiers = child == null ? null : child.getVariables()
                                                                .stream()
                                                                .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                          ClausewitzVariable::getAsDouble,
                                                                                          (a, b) -> b,
                                                                                          LinkedHashMap::new));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHarshLocalizedName() {
        return harshLocalizedName;
    }

    void setHarshLocalizedName(String harshLocalizedName) {
        this.harshLocalizedName = harshLocalizedName;
    }

    public String getConcilatoryLocalizedName() {
        return concilatoryLocalizedName;
    }

    void setConcilatoryLocalizedName(String concilatoryLocalizedName) {
        this.concilatoryLocalizedName = concilatoryLocalizedName;
    }

    public Map<String, Double> getHarshModifiers() {
        return this.harshModifiers == null ? new LinkedHashMap<>() : this.harshModifiers;
    }

    public void addHarshModifier(String modifier, Double quantity) {
        if (this.harshModifiers == null) {
            this.harshModifiers = new LinkedHashMap<>();
        }

        this.harshModifiers.put(modifier, quantity);
    }

    public void removeHarshModifier(String modifier) {
        if (this.harshModifiers != null) {
            this.harshModifiers.remove(modifier);
        }
    }

    public Map<String, Double> getConcilatoryModifiers() {
        return this.concilatoryModifiers == null ? new LinkedHashMap<>() : this.concilatoryModifiers;
    }

    public void addConcilatoryModifier(String modifier, Double quantity) {
        if (this.concilatoryModifiers == null) {
            this.concilatoryModifiers = new LinkedHashMap<>();
        }

        this.concilatoryModifiers.put(modifier, quantity);
    }

    public void removeConcilatoryModifier(String modifier) {
        if (this.concilatoryModifiers != null) {
            this.concilatoryModifiers.remove(modifier);
        }
    }
}
