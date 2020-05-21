package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.EnumMap;
import java.util.Map;

public class NavalCombatant extends Combatant {

    public NavalCombatant(ClausewitzItem item) {
        super(item);
    }

    public Map<Losses, Double> getLosses() {
        Map<Losses, Double> lossesMap = new EnumMap<>(Losses.class);
        ClausewitzList lossesList = this.item.getList("losses_type");

        if (lossesList != null) {
            for (Losses losses : Losses.values()) {
                lossesMap.put(losses, lossesList.getAsDouble(losses.ordinal()));
            }
        }

        return lossesMap;
    }

    public Double getGalley() {
        return this.item.getVarAsDouble("galley");
    }

    public Double getLightShip() {
        return this.item.getVarAsDouble("light_ship");
    }

    public Double getHeavyShip() {
        return this.item.getVarAsDouble("heavy_ship");
    }

    public Double getTransport() {
        return this.item.getVarAsDouble("transport");
    }
}
