package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class EstateModifier {

    private final ClausewitzItem item;

    private final String modifierName;

    public EstateModifier(ClausewitzItem item, String modifierName) {
        this.item = item;
        this.modifierName = modifierName;
    }

    public Optional<String> getDesc() {
        return this.item.getVarAsString("desc");
    }

    public void setDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            this.item.removeVariable("desc");
        } else {
            this.item.setVariable("desc", desc);
        }
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public double getAmount() {
        return this.item.getVarAsDouble(this.modifierName).orElse(0d);
    }

    public void setAmount(double amount) {
        this.item.setVariable(this.modifierName, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EstateModifier estateModifier)) {
            return false;
        }

        return Objects.equals(getDesc(), estateModifier.getDesc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDesc());
    }

    @Override
    public String toString() {
        return getDesc().orElse("");
    }
}
