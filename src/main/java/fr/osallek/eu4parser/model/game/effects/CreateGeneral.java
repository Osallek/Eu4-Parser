package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//create_admiral, create_general
public class CreateGeneral {

    private final ClausewitzItem item;

    public CreateGeneral(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getTradition() {
        return this.item.getVarAsInt("tradition");
    }

    public Integer getAddFire() {
        return this.item.getVarAsInt("add_fire");
    }
    public Integer getAddShock() {
        return this.item.getVarAsInt("add_shock");
    }
    public Integer getAddManuever() {
        return this.item.getVarAsInt("add_manuever");
    }
    public Integer getAddSiege() {
        return this.item.getVarAsInt("add_siege");
    }

    public String getCulture() {
        return this.item.getVarAsString("culture");
    }
}
