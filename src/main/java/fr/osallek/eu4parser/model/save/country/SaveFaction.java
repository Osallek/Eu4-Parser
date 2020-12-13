package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Faction;
import fr.osallek.eu4parser.model.game.Game;

public class SaveFaction {

    private final Game game;

    private final ClausewitzItem item;

    public SaveFaction(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Double getInfluence() {
        return this.item.getVarAsDouble("influence");
    }

    public void setInfluence(double influence) {
        this.item.setVariable("influence", Math.min(Math.max(0, influence), 100));
    }

    public Faction getType() {
        return this.game.getFaction(this.item.getVarAsString("type"));
    }

    public Double getOldInfluence() {
        return this.item.getVarAsDouble("old_influence");
    }

    public void setOldInfluence(double influence) {
        this.item.setVariable("old_influence", Math.min(Math.max(0, influence), 100));
    }
}
