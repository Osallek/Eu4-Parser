package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class TechGroup extends Nodded {

    private final ClausewitzItem item;

    public TechGroup(ClausewitzItem item, FileNode fileNode) {
        super(fileNode);
        this.item = item;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public int getStartLevel() {
        return this.item.getVarAsInt("start_level");
    }

    public void setStartLevel(int startLevel) {
        this.item.setVariable("start_level", startLevel);
    }

    public double getStartCostModifier() {
        return this.item.getVarAsDouble("start_cost_modifier");
    }

    public void setStartCostModifier(double startCostModifier) {
        this.item.setVariable("start_cost_modifier", startCostModifier);
    }

    public boolean isPrimitive() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("is_primitive"));
    }

    public void setPrimitive(Boolean primitive) {
        if (primitive == null) {
            this.item.removeVariable("is_primitive");
        } else{
            this.item.setVariable("is_primitive", primitive);
        }
    }

    public String getNationDesignerUnitType() {
        return this.item.getVarAsString("nation_designer_unit_type");
    }

    public void setNationDesignerUnitType(String nationDesignerUnitType) {
        if (StringUtils.isBlank(nationDesignerUnitType)) {
            this.item.removeVariable("nation_designer_unit_type");
        } else{
            this.item.setVariable("nation_designer_unit_type", nationDesignerUnitType);
        }
    }

    public NationDesignerCost getNationDesignerCost() {
        ClausewitzItem costChild = this.item.getChild("nation_designer_cost");
        return costChild == null ? null : new NationDesignerCost(costChild);
    }

    public void setNationDesignerCostValue(NationDesignerCost nationDesignerCost) {
        if (nationDesignerCost == null) {
            this.item.removeChild("nation_designer_trigger");
            return;
        }

        ClausewitzItem costChild = this.item.getChild("nation_designer_cost");
        //Todo NationDesignerCost => item
    }

    public Condition getNationDesignerTrigger() {
        ClausewitzItem triggerChild = this.item.getChild("nation_designer_trigger");
        return triggerChild == null ? null : new Condition(triggerChild);
    }

    public void setNationDesignerTrigger(Condition condition) {
        if (condition == null) {
            this.item.removeChild("nation_designer_trigger");
            return;
        }

        ClausewitzItem triggerChild = this.item.getChild("nation_designer_trigger");
        //Todo Condition => item
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TechGroup)) {
            return false;
        }

        TechGroup techGroup = (TechGroup) o;

        return Objects.equals(getName(), techGroup.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
