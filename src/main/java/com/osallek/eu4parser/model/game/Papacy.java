package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Papacy {

    private final ClausewitzItem item;

    private List<PapacyConcession> concessions;

    public Papacy(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public List<PapacyConcession> getConcessions() {
        return concessions;
    }

    public PapacyConcession getConcession(int i) {
        return this.concessions.get(i);
    }

    public String getPapacyTag() {
        return this.item.getVarAsString("papal_tag");
    }

    public void setPapacyTag(String tag) {
        this.item.setVariable("papal_tag", tag);
    }

    public Integer getElectionCost() {
        return this.item.getVarAsInt("election_cost");
    }

    public void setElectionCost(int electionCost) {
        this.item.setVariable("election_cost", electionCost);
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

    public void addNeutralModifier(String modifier, Double quantity) {
        ClausewitzItem neutralModifiersItem = this.item.getChild("neutral");

        if (neutralModifiersItem != null) {
            neutralModifiersItem.setVariable(modifier, quantity);
        }
    }

    public void removeNeutralModifier(String modifier) {
        ClausewitzItem neutralModifiersItem = this.item.getChild("neutral");

        if (neutralModifiersItem != null) {
            neutralModifiersItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getConcilatoryModifiers() {
        ClausewitzItem concilatoryModifiersItem = this.item.getChild("concilatory");

        if (concilatoryModifiersItem != null) {
            return concilatoryModifiersItem.getVariables()
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

    private void refreshAttributes() {
        ClausewitzItem concessionsItem = this.item.getChild("concessions");

        if (concessionsItem != null) {
            this.concessions = concessionsItem.getChildren()
                                              .stream()
                                              .map(PapacyConcession::new)
                                              .collect(Collectors.toList());
        }
    }
}
