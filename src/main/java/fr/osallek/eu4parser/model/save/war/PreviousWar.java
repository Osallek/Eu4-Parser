package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

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
