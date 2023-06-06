package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.Save;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class NavalCombatant extends Combatant {

    public NavalCombatant(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Map<Losses, Double> getLosses() {
        return this.item.getList("losses_type").map(list -> {
            Map<Losses, Double> lossesMap = new EnumMap<>(Losses.class);

            for (Losses losses : Losses.values()) {
                list.getAsDouble(losses.ordinal()).ifPresent(integer -> lossesMap.put(losses, integer));
            }

            return lossesMap;
        }).orElse(new EnumMap<>(Losses.class));
    }

    public Optional<Double> getGalley() {
        return this.item.getVarAsDouble("galley");
    }

    public Optional<Double> getLightShip() {
        return this.item.getVarAsDouble("light_ship");
    }

    public Optional<Double> getHeavyShip() {
        return this.item.getVarAsDouble("heavy_ship");
    }

    public Optional<Double> getTransport() {
        return this.item.getVarAsDouble("transport");
    }
}
