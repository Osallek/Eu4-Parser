package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.NumbersUtils;

import java.util.EnumMap;
import java.util.Map;

public class PowerSpentIndexed {

    private final ClausewitzItem item;

    public PowerSpentIndexed(ClausewitzItem item) {
        this.item = item;
    }

    public Map<PowerSpent, Integer> getPowerSpent() {
        Map<PowerSpent, Integer> powerSpentIndexed = new EnumMap<>(PowerSpent.class);

        for (PowerSpent powerSpent : PowerSpent.values()) {
            powerSpentIndexed.put(powerSpent, NumbersUtils.intOrDefault(this.item.getVarAsInt(String.valueOf(powerSpent.ordinal()))));
        }

        return powerSpentIndexed;
    }

    public Integer getPowerSpent(PowerSpent powerSpent) {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt(String.valueOf(powerSpent.ordinal())));
    }
}
