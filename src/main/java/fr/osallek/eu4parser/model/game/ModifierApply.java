package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ModifierApply {

    private final ClausewitzItem item;

    public ModifierApply(ClausewitzItem item) {
        this.item = item;
    }

    public String getModifier() {
        return this.item.getVarAsString("name").orElse("");
    }

    public void setModifier(String modifier) {
        this.item.setVariable("name", modifier);
    }

    public int getDuration() {
        return this.item.getVarAsInt("duration").orElse(0);
    }

    public void setDuration(int duration) {
        this.item.setVariable("duration", duration);
    }
}
