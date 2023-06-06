package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.Save;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class LandCombatant extends Combatant {

    public LandCombatant(ClausewitzItem item, Save save) {
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

    public Optional<Double> getCavalry() {
        return this.item.getVarAsDouble("cavalry");
    }

    public Optional<Double> getArtillery() {
        return this.item.getVarAsDouble("artillery");
    }

    public Optional<Double> getInfantry() {
        return this.item.getVarAsDouble("infantry");
    }
}
