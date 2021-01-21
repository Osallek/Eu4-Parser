package fr.osallek.eu4parser.model;

import org.apache.commons.lang3.StringUtils;

public enum UnitType {
    INFANTRY, CAVALRY, ARTILLERY, HEAVY_SHIP, LIGHT_SHIP, GALLEY, TRANSPORT;

    public static UnitType value(String s) {
        return StringUtils.isBlank(s) ? null : UnitType.valueOf(s.toUpperCase());
    }
}
