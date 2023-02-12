package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_institution_embracement
public class WhichValue {

    protected final ClausewitzItem item;

    public WhichValue(ClausewitzItem item) {
        this.item = item;
    }

    public String getWhich() {
        return this.item.getVarAsString("which");
    }

    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
