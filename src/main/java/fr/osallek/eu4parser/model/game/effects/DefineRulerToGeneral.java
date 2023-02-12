package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//define_ruler_to_general, define_heir_to_general
public class DefineRulerToGeneral {

    protected final ClausewitzItem item;

    public DefineRulerToGeneral(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getFire() {
        return this.item.getVarAsInt("fire");
    }

    public Integer getShock() {
        return this.item.getVarAsInt("shock");
    }

    public Integer getManuever() {
        return this.item.getVarAsInt("manuever");
    }

    public Integer getSiege() {
        return this.item.getVarAsInt("siege");
    }
}
