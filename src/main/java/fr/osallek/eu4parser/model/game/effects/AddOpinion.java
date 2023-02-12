package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_opinion, reverse_add_opinion, remove_opinion, reverse_remove_opinion
public class AddOpinion {

    private final ClausewitzItem item;

    public AddOpinion(ClausewitzItem item) {
        this.item = item;
    }

    public String getWho() {
        return this.item.getVarAsString("who");
    }

    public String getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public Integer getYears() {
        return this.item.getVarAsInt("years");
    }
}
