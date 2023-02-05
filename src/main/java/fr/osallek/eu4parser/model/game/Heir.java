package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class Heir extends Monarch {

    public Heir(ClausewitzItem item, Country country) {
        super(item, country);
    }

    public Double getClaim() {
        return this.item.getVarAsDouble("claim");
    }

    public void setClaim(Double claim) {
        if (claim < 0) {
            claim = 0d;
        } else if (claim > 100) {
            claim = 100d;
        }

        this.item.setVariable("claim", claim);
    }
}
