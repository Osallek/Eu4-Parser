package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public class TechGroup {

    private final String name;

    private String localizedName;

    private final int startLevel;

    private final double startCostModifier;

    private final NationDesignerCost nationDesignerCost;

    private final boolean isPrimitive;

    private final String nationDesignerUnitType;

    public TechGroup(ClausewitzItem item) {
        this.name = item.getName();
        this.startLevel = item.getVarAsInt("start_level");
        this.startCostModifier = item.getVarAsDouble("start_cost_modifier");
        this.isPrimitive = BooleanUtils.toBoolean(item.getVarAsBool("is_primitive"));
        this.nationDesignerUnitType = item.getVarAsString("nation_designer_unit_type");

        ClausewitzItem triggerChild = item.getChild("nation_designer_trigger");
        ClausewitzItem costChild = item.getChild("nation_designer_cost");
        this.nationDesignerCost = costChild == null ? null : new NationDesignerCost(costChild, triggerChild);
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public double getStartCostModifier() {
        return startCostModifier;
    }

    public NationDesignerCost getNationDesignerCost() {
        return nationDesignerCost;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public String getNationDesignerUnitType() {
        return nationDesignerUnitType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TechGroup)) {
            return false;
        }

        TechGroup area = (TechGroup) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
