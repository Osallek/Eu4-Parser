package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PapacyConcession {

    private final ClausewitzItem item;

    private String harshLocalizedName;

    private String concilatoryLocalizedName;

    public PapacyConcession(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
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
        ClausewitzItem harshModifiersItem = this.item.getChild("harsh");

        if (harshModifiersItem != null) {
            return harshModifiersItem.getVariables()
                                     .stream()
                                     .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                               ClausewitzVariable::getAsDouble,
                                                               (a, b) -> b,
                                                               LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addHarshModifier(String modifier, Double quantity) {
        ClausewitzItem harshModifiersItem = this.item.getChild("harsh");

        if (harshModifiersItem != null) {
            harshModifiersItem.setVariable(modifier, quantity);
        }
    }

    public void removeHarshModifier(String modifier) {
        ClausewitzItem harshModifiersItem = this.item.getChild("harsh");

        if (harshModifiersItem != null) {
            harshModifiersItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getNeutralModifiers() {
        ClausewitzItem neutralModifiersItem = this.item.getChild("neutral");

        if (neutralModifiersItem != null) {
            return neutralModifiersItem.getVariables()
                                     .stream()
                                     .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                               ClausewitzVariable::getAsDouble,
                                                               (a, b) -> b,
                                                               LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addConcilatoryModifier(String modifier, Double quantity) {
        ClausewitzItem concilatoryModifiersItem = this.item.getChild("concilatory");

        if (concilatoryModifiersItem != null) {
            concilatoryModifiersItem.setVariable(modifier, quantity);
        }
    }

    public void removeConcilatoryModifier(String modifier) {
        ClausewitzItem concilatoryModifiersItem = this.item.getChild("concilatory");

        if (concilatoryModifiersItem != null) {
            concilatoryModifiersItem.removeVariable(modifier);
        }
    }
}
