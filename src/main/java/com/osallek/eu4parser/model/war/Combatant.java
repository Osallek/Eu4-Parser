package com.osallek.eu4parser.model.war;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Combatant {

    private final ClausewitzItem item;

    public Combatant(ClausewitzItem item) {
        this.item = item;
    }

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
        return this.item.getVarAsString("country");
    }

    public String getCommander() {
        return this.item.getVarAsString("commander");
    }
}
