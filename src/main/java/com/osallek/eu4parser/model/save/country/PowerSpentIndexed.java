package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

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
            powerSpentIndexed.put(powerSpent, this.item.getVarAsInt(String.valueOf(powerSpent.ordinal())));
        }

        return powerSpentIndexed;
    }

    public Integer getPowerSpent(PowerSpent powerSpent) {
        return this.item.getVarAsInt(String.valueOf(powerSpent.ordinal()));
    }
}
