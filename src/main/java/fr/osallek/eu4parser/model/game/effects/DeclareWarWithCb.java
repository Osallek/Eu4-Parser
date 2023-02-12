package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class DeclareWarWithCb {

    private final ClausewitzItem item;

    public DeclareWarWithCb(ClausewitzItem item) {
        this.item = item;
    }

    public String getWho() {
        return this.item.getVarAsString("who");
    }

    public String getCasusBelli() {
        return this.item.getVarAsString("casus_belli");
    }

    public Integer getWarGoalProvince() {
        return this.item.getVarAsInt("war_goal_province");
    }

}
