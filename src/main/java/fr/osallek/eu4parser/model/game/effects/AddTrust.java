package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class AddTrust extends WhoValue {

    public AddTrust(ClausewitzItem item) {
        super(item);
    }

    public Boolean getMutual() {
        return this.item.getVarAsBool("mutual");
    }
}
