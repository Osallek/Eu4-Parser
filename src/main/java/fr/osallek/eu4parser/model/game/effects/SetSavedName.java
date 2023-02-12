package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class SetSavedName {

    private final ClausewitzItem item;

    public SetSavedName(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public String getKey() {
        return this.item.getVarAsString("key");
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public String getScope() {
        return this.item.getVarAsString("scope");
    }
}
