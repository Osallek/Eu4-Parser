package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.Save;

import java.util.EnumMap;
import java.util.Map;

public class NavalCombatant extends Combatant {

    public NavalCombatant(ClausewitzItem item, Save save) {
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
