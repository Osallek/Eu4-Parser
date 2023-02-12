package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//remove_loot, add_favors
public class WhoAmount {

    protected final ClausewitzItem item;

    public WhoAmount(ClausewitzItem item) {
        this.item = item;
    }

    public String getWho() {
        return this.item.getVarAsString("who");
    }

    public Integer getAmount() {
        return this.item.getVarAsInt("amount");
    }
}
