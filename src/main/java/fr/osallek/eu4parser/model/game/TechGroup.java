package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

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
        return this.item.getVarAsInt("start_level").orElse(0);
    }

    public void setStartLevel(int startLevel) {
        this.item.setVariable("start_level", startLevel);
    }

    public double getStartCostModifier() {
        return this.item.getVarAsDouble("start_cost_modifier").orElse(0d);
    }

    public void setStartCostModifier(double startCostModifier) {
        this.item.setVariable("start_cost_modifier", startCostModifier);
    }

    public boolean isPrimitive() {
        return this.item.getVarAsBool("is_primitive").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setPrimitive(Boolean primitive) {
        if (primitive == null) {
            this.item.removeVariable("is_primitive");
        } else{
            this.item.setVariable("is_primitive", primitive);
        }
    }

    public Optional<String> getNationDesignerUnitType() {
        return this.item.getVarAsString("nation_designer_unit_type");
    }

    public void setNationDesignerUnitType(String nationDesignerUnitType) {
        if (StringUtils.isBlank(nationDesignerUnitType)) {
            this.item.removeVariable("nation_designer_unit_type");
        } else{
            this.item.setVariable("nation_designer_unit_type", nationDesignerUnitType);
        }
    }

    public Optional<NationDesignerCost> getNationDesignerCost() {
        return this.item.getChild("nation_designer_cost").map(NationDesignerCost::new);
    }

    public Optional<ConditionAnd> getNationDesignerTrigger() {
        return this.item.getChild("nation_designer_trigger").map(ConditionAnd::new);
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

        if (!(o instanceof TechGroup techGroup)) {
            return false;
        }

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
