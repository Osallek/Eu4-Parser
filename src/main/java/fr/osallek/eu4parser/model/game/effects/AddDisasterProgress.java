package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class AddDisasterProgress {

    private final ClausewitzItem item;

    public AddDisasterProgress(ClausewitzItem item) {
        this.item = item;
    }

    public String getDisaster() {
        return this.item.getVarAsString("disaster");
    }


    public Integer getValue() {
        return this.item.getVarAsInt("value");
    }
}
