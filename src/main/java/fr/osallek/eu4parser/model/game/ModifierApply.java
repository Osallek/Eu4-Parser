package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ModifierApply {

    private final String modifier;

    private final int duration;

    public ModifierApply(ClausewitzItem item) {
        this.modifier = item.getVarAsString("name");
        this.duration = item.getVarAsInt("duration");
    }

    public String getModifier() {
        return modifier;
    }

    public int getDuration() {
        return duration;
    }
}
