package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record Combatant(ClausewitzItem item) {

    public Integer getCavalry() {
        return this.item.getVarAsInt("cavalry");
    }

    public Integer getArtillery() {
        return this.item.getVarAsInt("artillery");
    }

    public Integer getInfantry() {
        return this.item.getVarAsInt("infantry");
    }

    public Integer getGalley() {
        return this.item.getVarAsInt("galley");
    }

    public Integer getLightShip() {
        return this.item.getVarAsInt("light_ship");
    }

    public Integer getHeavyShip() {
        return this.item.getVarAsInt("heavy_ship");
    }

    public Integer getTransport() {
        return this.item.getVarAsInt("transport");
    }

    public Integer getLosses() {
        return this.item.getVarAsInt("losses");
    }

    public String getCountry() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("country"));
    }

    public String getCommander() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("commander"));
    }
}
