package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class SetGovernmentAndRank {

    private final ClausewitzItem item;

    public SetGovernmentAndRank(ClausewitzItem item) {
        this.item = item;
    }

    public String getGovernment() {
        return this.item.getVarAsString("government");
    }

    public Integer getRank() {
        return this.item.getVarAsInt("rank");
    }
}
