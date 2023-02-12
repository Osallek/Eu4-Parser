package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class SetSchoolOpinion {

    private final ClausewitzItem item;

    public SetSchoolOpinion(ClausewitzItem item) {
        this.item = item;
    }

    public String getWho() {
        return this.item.getVarAsString("who");
    }

    public Integer getOpinion() {
        return this.item.getVarAsInt("opinion");
    }
}
