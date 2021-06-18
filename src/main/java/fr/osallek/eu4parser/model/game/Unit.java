package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.UnitType;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Unit {
    
    private final String name;

    private final String localizedName;

    private final String unitType;

    private final UnitType type;

    private final Integer maneuver;

    private final Integer offensiveMorale;

    private final Integer defensiveMorale;

    private final Integer offensiveFire;

    private final Integer defensiveFire;

    private final Integer offensiveShock;

    private final Integer defensiveShock;

    private final Integer hullSize;

    private final Integer baseCannons;

    private final Double sailSpeed;

    private final Integer spriteLevel;

    public Unit(ClausewitzItem item, UnaryOperator<String> localizationFunction) {
        this.name = item.getName();
        this.localizedName = localizationFunction.apply(this.name);
        this.unitType = item.getVarAsString("unit_type");
        this.type = UnitType.value(item.getVarAsString("type"));
        this.maneuver = item.getVarAsInt("maneuver");
        this.offensiveMorale = item.getVarAsInt("offensive_morale");
        this.defensiveMorale = item.getVarAsInt("defensive_morale");
        this.offensiveFire = item.getVarAsInt("offensive_fire");
        this.defensiveFire = item.getVarAsInt("defensive_fire");
        this.offensiveShock = item.getVarAsInt("offensive_shock");
        this.defensiveShock = item.getVarAsInt("defensive_shock");
        this.hullSize = item.getVarAsInt("hull_size");
        this.baseCannons = item.getVarAsInt("base_cannons");
        this.sailSpeed = item.getVarAsDouble("sail_speed");
        this.spriteLevel = item.getVarAsInt("sprite_level");
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getUnitType() {
        return unitType;
    }

    public UnitType getType() {
        return type;
    }

    public Integer getManeuver() {
        return maneuver;
    }

    public Integer getOffensiveMorale() {
        return offensiveMorale;
    }

    public Integer getDefensiveMorale() {
        return defensiveMorale;
    }

    public Integer getOffensiveFire() {
        return offensiveFire;
    }

    public Integer getDefensiveFire() {
        return defensiveFire;
    }

    public Integer getOffensiveShock() {
        return offensiveShock;
    }

    public Integer getDefensiveShock() {
        return defensiveShock;
    }

    public Integer getHullSize() {
        return hullSize;
    }

    public Integer getBaseCannons() {
        return baseCannons;
    }

    public Double getSailSpeed() {
        return sailSpeed;
    }

    public Integer getSpriteLevel() {
        return spriteLevel;
    }
}
