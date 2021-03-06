package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.Save;

import java.util.EnumMap;
import java.util.Map;

public class LandCombatant extends Combatant {

    public LandCombatant(ClausewitzItem item, Save save) {
        super(item, save);
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
