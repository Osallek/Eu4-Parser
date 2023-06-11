package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Faction;
import fr.osallek.eu4parser.model.game.Game;

import java.util.Optional;

public record SaveFaction(ClausewitzItem item, Game game) {

    public Optional<Double> getInfluence() {
        return this.item.getVarAsDouble("influence");
    }

    public void setInfluence(double influence) {
        this.item.setVariable("influence", Math.min(Math.max(0, influence), 100));
    }

    public Optional<Faction> getType() {
        return this.item.getVarAsString("type").map(this.game::getFaction);
    }

    public Optional<Double> getOldInfluence() {
        return this.item.getVarAsDouble("old_influence");
    }

    public void setOldInfluence(double influence) {
        this.item.setVariable("old_influence", Math.min(Math.max(0, influence), 100));
    }
}
