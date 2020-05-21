package com.osallek.eu4parser.model.save.war;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class PreviousWar extends ActiveWar {

    public PreviousWar(ClausewitzItem item) {
        super(item);
    }

    public Integer getOutcome() {
        return this.item.getVarAsInt("outcome");
    }
}
