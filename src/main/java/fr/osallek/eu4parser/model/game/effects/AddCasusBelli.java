package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_casus_belli, reverse_add_casus_belli, remove_casus_belli, reverse_remove_casus_belli
public class AddCasusBelli extends Type {

    public AddCasusBelli(ClausewitzItem item) {
        super(item);
    }

    public String getTarget() {
        return this.item.getVarAsString("target");
    }

    public Integer getMonths() {
        return this.item.getVarAsInt("months");
    }
}
