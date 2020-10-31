package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Papacy {

    private String papalTag;

    private Integer electionCost;

    private Modifiers harshModifiers;

    private Modifiers neutralModifiers;

    private Modifiers concilatoryModifiers;

    private final List<PapacyConcession> concessions;

    public Papacy(ClausewitzItem item) {
        this.papalTag = item.getVarAsString("papal_tag");
        this.electionCost = item.getVarAsInt("election_cost");
        this.harshModifiers = new Modifiers(item.getChild("harsh"));
        this.neutralModifiers = new Modifiers(item.getChild("neutral"));
        this.concilatoryModifiers = new Modifiers(item.getChild("concilatory"));

        ClausewitzItem child = item.getChild("concessions");
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

    public Modifiers getHarshModifiers() {
        return this.harshModifiers;
    }

    public void addHarshModifier(String modifier, String quantity) {
        if (this.harshModifiers == null) {
            this.harshModifiers = new Modifiers();
        }

        this.harshModifiers.add(modifier, quantity);
    }

    public void removeHarshModifier(String modifier) {
        if (this.harshModifiers != null) {
            this.harshModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getNeutralModifiers() {
        return this.neutralModifiers;
    }

    public void addNeutralModifier(String modifier, String quantity) {
        if (this.neutralModifiers == null) {
            this.neutralModifiers = new Modifiers();
        }

        this.neutralModifiers.add(modifier, quantity);
    }

    public void removeNeutralModifier(String modifier) {
        if (this.neutralModifiers != null) {
            this.neutralModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getConcilatoryModifiers() {
        return this.concilatoryModifiers;
    }

    public void addConcilatoryModifier(String modifier, String quantity) {
        if (this.concilatoryModifiers == null) {
            this.concilatoryModifiers = new Modifiers();
        }

        this.concilatoryModifiers.add(modifier, quantity);
    }

    public void removeConcilatoryModifier(String modifier) {
        if (this.concilatoryModifiers != null) {
            this.concilatoryModifiers.removeModifier(modifier);
        }
    }
}
