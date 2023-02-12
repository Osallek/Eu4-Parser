package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class AddDisasterModifier {

    private final ClausewitzItem item;

    public AddDisasterModifier(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public String getDisaster() {
        return this.item.getVarAsString("disaster");
    }


    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }
}
