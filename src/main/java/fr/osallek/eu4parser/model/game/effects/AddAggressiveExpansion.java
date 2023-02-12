package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_aggressive_expansion, reverse_add_aggressive_expansion
public class AddAggressiveExpansion extends WhoValue {

    public AddAggressiveExpansion(ClausewitzItem item) {
        super(item);
    }

    public Boolean applyCalc() {
        return this.item.getVarAsBool("apply_calc");
    }
}
