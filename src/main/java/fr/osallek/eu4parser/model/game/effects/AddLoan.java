package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class AddLoan {

    private final ClausewitzItem item;

    public AddLoan(ClausewitzItem item) {
        this.item = item;
    }

    public Double getInterestModifier() {
        return this.item.getVarAsDouble("interest_modifier");
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public Boolean getFixedInterest() {
        return this.item.getVarAsBool("fixed_interest");
    }
}
