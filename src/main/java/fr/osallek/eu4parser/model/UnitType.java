package fr.osallek.eu4parser.model;

import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.lang3.StringUtils;

public enum UnitType {
    INFANTRY, CAVALRY, ARTILLERY, HEAVY_SHIP, LIGHT_SHIP, GALLEY, TRANSPORT;

    private static final UnitType[] LAND_NO_ARTILLERY = new UnitType[] {INFANTRY, CAVALRY};

    private static final UnitType[] LAND_ARTILLERY = new UnitType[] {INFANTRY, CAVALRY, ARTILLERY};

    private static final UnitType[] NAVY = new UnitType[] {HEAVY_SHIP, LIGHT_SHIP, GALLEY, TRANSPORT};

    public static UnitType value(String s) {
        return StringUtils.isBlank(s) ? null : UnitType.valueOf(s.toUpperCase());
    }

    public static UnitType[] land(SaveCountry country) {
        return country.getSubUnit().getSubUnit(ARTILLERY) == null ? LAND_NO_ARTILLERY : LAND_ARTILLERY;
    }

    public static UnitType[] navy() {
        return NAVY;
    }
}
