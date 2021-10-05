package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.UnitType;
import org.apache.commons.lang3.StringUtils;

public class Unit {

    private final ClausewitzItem item;

    public Unit(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public String getUnitType() {
        return this.item.getVarAsString("unit_type");
    }

    public void setUnitType(String unitType) {
        if (StringUtils.isBlank(unitType)) {
            this.item.removeVariable("unit_type");
        } else {
            this.item.setVariable("unit_type", unitType);
        }
    }

    public UnitType getType() {
        return UnitType.value(this.item.getVarAsString("type"));
    }

    public void setType(UnitType type) {
        this.item.setVariable("type", type.name().toLowerCase());
    }

    public Integer getManeuver() {
        return this.item.getVarAsInt("maneuver");
    }

    public void setManeuver(Integer maneuver) {
        if (maneuver == null) {
            this.item.removeVariable("maneuver");
        } else {
            this.item.setVariable("maneuver", maneuver);
        }
    }
    
    public Integer getOffensiveMorale() {
        return this.item.getVarAsInt("offensive_morale");
    }

    public void setOffensiveMorale(Integer offensiveMorale) {
        if (offensiveMorale == null) {
            this.item.removeVariable("offensive_morale");
        } else {
            this.item.setVariable("offensive_morale", offensiveMorale);
        }
    }

    public Integer getDefensiveMorale() {
        return this.item.getVarAsInt("defensive_morale");
    }

    public void setDefensiveMorale(Integer defensiveMorale) {
        if (defensiveMorale == null) {
            this.item.removeVariable("defensive_morale");
        } else {
            this.item.setVariable("defensive_morale", defensiveMorale);
        }
    }

    public Integer getOffensiveFire() {
        return this.item.getVarAsInt("offensive_fire");
    }

    public void setOffensiveFire(Integer offensiveFire) {
        if (offensiveFire == null) {
            this.item.removeVariable("offensive_fire");
        } else {
            this.item.setVariable("offensive_fire", offensiveFire);
        }
    }

    public Integer getDefensiveFire() {
        return this.item.getVarAsInt("defensive_fire");
    }

    public void setDefensiveFire(Integer defensiveFire) {
        if (defensiveFire == null) {
            this.item.removeVariable("defensive_fire");
        } else {
            this.item.setVariable("defensive_fire", defensiveFire);
        }
    }

    public Integer getOffensiveShock() {
        return this.item.getVarAsInt("offensive_shock");
    }

    public void setOffensiveShock(Integer offensiveShock) {
        if (offensiveShock == null) {
            this.item.removeVariable("offensive_shock");
        } else {
            this.item.setVariable("offensive_shock", offensiveShock);
        }
    }
    
    public Integer getDefensiveShock() {
        return this.item.getVarAsInt("defensive_shock");
    }

    public void setDefensiveShock(Integer defensiveShock) {
        if (defensiveShock == null) {
            this.item.removeVariable("defensive_shock");
        } else {
            this.item.setVariable("defensive_shock", defensiveShock);
        }
    }
    
    public Integer getHullSize() {
        return this.item.getVarAsInt("hull_size");
    }

    public void setHullSize(Integer hullSize) {
        if (hullSize == null) {
            this.item.removeVariable("hull_size");
        } else {
            this.item.setVariable("hull_size", hullSize);
        }
    }
    
    public Integer getBaseCannons() {
        return this.item.getVarAsInt("base_cannons");
    }

    public void setBaseCannons(Integer baseCannons) {
        if (baseCannons == null) {
            this.item.removeVariable("base_cannons");
        } else {
            this.item.setVariable("base_cannons", baseCannons);
        }
    }

    public Double getSailSpeed() {
        return this.item.getVarAsDouble("sail_speed");
    }

    public void setSailSpeed(Double sailSpeed) {
        if (sailSpeed == null) {
            this.item.removeVariable("sail_speed");
        } else {
            this.item.setVariable("sail_speed", sailSpeed);
        }
    }

    public Integer getSpriteLevel() {
        return this.item.getVarAsInt("sprite_level");
    }

    public void setSpriteLevel(Integer spriteLevel) {
        if (spriteLevel == null) {
            this.item.removeVariable("sprite_level");
        } else {
            this.item.setVariable("sprite_level", spriteLevel);
        }
    }
}
