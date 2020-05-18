package com.osallek.eu4parser.model.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.EnumMap;
import java.util.Map;

public class LandCombatant extends Combatant {

    public LandCombatant(ClausewitzItem item) {
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

    public Double getCavalry() {
        return this.item.getVarAsDouble("cavalry");
    }

    public Double getArtillery() {
        return this.item.getVarAsDouble("artillery");
    }

    public Double getInfantry() {
        return this.item.getVarAsDouble("infantry");
    }
}
