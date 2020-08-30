package com.osallek.eu4parser.model.save.war;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

public class PreviousWar extends ActiveWar {

    public PreviousWar(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Integer getOutcome() {
        return this.item.getVarAsInt("outcome");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
