package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Papacy {

    private final ClausewitzItem item;

    public Papacy(ClausewitzItem item) {
        this.item = item;
    }

    public List<PapacyConcession> getConcessions() {
        return this.item.getChild("concessions").map(ClausewitzItem::getChildren).stream().flatMap(Collection::stream).map(PapacyConcession::new).toList();
    }

    public Optional<String> getPapacyTag() {
        return this.item.getVarAsString("papal_tag");
    }

    public void setPapacyTag(String tag) {
        this.item.setVariable("papal_tag", tag);
    }

    public Optional<Integer> getElectionCost() {
        return this.item.getVarAsInt("election_cost");
    }

    public void setElectionCost(int electionCost) {
        this.item.setVariable("election_cost", electionCost);
    }

    public Optional<Modifiers> getHarshModifiers() {
        return this.item.getChild("harsh").map(Modifiers::new);
    }

    public Optional<Modifiers> getNeutralModifiers() {
        return this.item.getChild("neutral").map(Modifiers::new);
    }

    public Optional<Modifiers> getConcilatoryModifiers() {
        return this.item.getChild("concilatory").map(Modifiers::new);
    }
}
