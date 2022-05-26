package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;

public class Papacy {

    private final ClausewitzItem item;

    public Papacy(ClausewitzItem item) {
        this.item = item;
    }

    public List<PapacyConcession> getConcessions() {
        ClausewitzItem child = this.item.getChild("concessions");
        return child == null ? null : child.getChildren()
                                           .stream()
                                           .map(PapacyConcession::new)
                                           .toList();
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

    public Modifiers getHarshModifiers() {
        return new Modifiers(this.item.getChild("harsh"));
    }

    public Modifiers getNeutralModifiers() {
        return new Modifiers(this.item.getChild("neutral"));
    }

    public Modifiers getConcilatoryModifiers() {
        return new Modifiers(this.item.getChild("concilatory"));
    }
}
