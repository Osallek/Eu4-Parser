package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record Combatant(ClausewitzItem item) {

    public Optional<Integer> getCavalry() {
        return this.item.getVarAsInt("cavalry");
    }

    public Optional<Integer> getArtillery() {
        return this.item.getVarAsInt("artillery");
    }

    public Optional<Integer> getInfantry() {
        return this.item.getVarAsInt("infantry");
    }

    public Optional<Integer> getGalley() {
        return this.item.getVarAsInt("galley");
    }

    public Optional<Integer> getLightShip() {
        return this.item.getVarAsInt("light_ship");
    }

    public Optional<Integer> getHeavyShip() {
        return this.item.getVarAsInt("heavy_ship");
    }

    public Optional<Integer> getTransport() {
        return this.item.getVarAsInt("transport");
    }

    public Optional<Integer> getLosses() {
        return this.item.getVarAsInt("losses");
    }

    public Optional<String> getCountry() {
        return this.item.getVarAsString("country").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getCommander() {
        return this.item.getVarAsString("commander").map(ClausewitzUtils::removeQuotes);
    }
}
