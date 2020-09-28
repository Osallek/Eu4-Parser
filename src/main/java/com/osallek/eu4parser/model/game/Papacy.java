package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Papacy {

    private String papalTag;

    private Integer electionCost;

    private Map<String, String> harshModifiers;

    private Map<String, String> neutralModifiers;

    private Map<String, String> concilatoryModifiers;

    private List<PapacyConcession> concessions;

    public Papacy(ClausewitzItem item) {
        this.papalTag = item.getVarAsString("papal_tag");
        this.electionCost = item.getVarAsInt("election_cost");
        ClausewitzItem child = item.getChild("harsh");
        this.harshModifiers = child == null ? null : child.getVariables()
                                                          .stream()
                                                          .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                    ClausewitzVariable::getValue,
                                                                                    (a, b) -> b,
                                                                                    LinkedHashMap::new));
        child = item.getChild("neutral");
        this.neutralModifiers = child == null ? null : child.getVariables()
                                                            .stream()
                                                            .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                      ClausewitzVariable::getValue,
                                                                                      (a, b) -> b,
                                                                                      LinkedHashMap::new));
        child = item.getChild("concilatory");
        this.concilatoryModifiers = child == null ? null : child.getVariables()
                                                                .stream()
                                                                .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                          ClausewitzVariable::getValue,
                                                                                          (a, b) -> b,
                                                                                          LinkedHashMap::new));


        child = item.getChild("concessions");
        this.concessions = child == null ? null : child.getChildren()
                                                       .stream()
                                                       .map(PapacyConcession::new)
                                                       .collect(Collectors.toList());
    }

    public List<PapacyConcession> getConcessions() {
        return this.concessions == null ? new ArrayList<>() : this.concessions;
    }

    public PapacyConcession getConcession(int i) {
        if (this.concessions == null) {
            return null;
        }

        return this.concessions.get(i);
    }

    public String getPapacyTag() {
        return this.papalTag;
    }

    public void setPapacyTag(String tag) {
        this.papalTag = tag;
    }

    public Integer getElectionCost() {
        return this.electionCost;
    }

    public void setElectionCost(int electionCost) {
        this.electionCost = electionCost;
    }

    public Map<String, String> getHarshModifiers() {
        return this.harshModifiers == null ? new LinkedHashMap<>() : this.harshModifiers;
    }

    public void addHarshModifier(String modifier, String quantity) {
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

    public Map<String, String> getNeutralModifiers() {
        return this.neutralModifiers == null ? new LinkedHashMap<>() : this.neutralModifiers;
    }

    public void addNeutralModifier(String modifier, String quantity) {
        if (this.neutralModifiers == null) {
            this.neutralModifiers = new LinkedHashMap<>();
        }

        this.neutralModifiers.put(modifier, quantity);
    }

    public void removeNeutralModifier(String modifier) {
        if (this.neutralModifiers != null) {
            this.neutralModifiers.remove(modifier);
        }
    }

    public Map<String, String> getConcilatoryModifiers() {
        return this.concilatoryModifiers == null ? new LinkedHashMap<>() : this.concilatoryModifiers;
    }

    public void addConcilatoryModifier(String modifier, String quantity) {
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
