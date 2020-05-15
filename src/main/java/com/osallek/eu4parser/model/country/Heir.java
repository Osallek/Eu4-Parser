package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Heir extends Monarch {

    public Heir(ClausewitzItem item) {
        super(item);
    }

    public Integer getClaim() {
        return this.item.getVarAsInt("claim");
    }

    public void setClaim(int claim) {
        if (claim < 0) {
            claim = 0;
        } else if (claim > 100) {
            claim = 100;
        }

        this.item.setVariable("claim", claim);
    }
}
