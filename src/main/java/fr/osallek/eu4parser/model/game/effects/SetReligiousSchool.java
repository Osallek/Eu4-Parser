package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class SetReligiousSchool {

    private final ClausewitzItem item;

    public SetReligiousSchool(ClausewitzItem item) {
        this.item = item;
    }

    public String getGroup() {
        return this.item.getVarAsString("group");
    }

    public String getSchool() {
        return this.item.getVarAsString("school");
    }
}
