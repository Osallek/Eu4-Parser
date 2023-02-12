package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_spy_network_from, add_spy_network_in
public class WhoType {

    protected final ClausewitzItem item;

    public WhoType(ClausewitzItem item) {
        this.item = item;
    }

    public String getWho() {
        return this.item.getVarAsString("who");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
