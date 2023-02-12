package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//kill_leader, destroy_great_project
public class Type {

    protected final ClausewitzItem item;

    public Type(ClausewitzItem item) {
        this.item = item;
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }
}
