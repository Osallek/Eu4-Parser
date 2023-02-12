package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_spy_network_from, add_spy_network_in
public class WhoValue {

    protected final ClausewitzItem item;

    public WhoValue(ClausewitzItem item) {
        this.item = item;
    }

    public String getWho() {
        return this.item.getVarAsString("who");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
