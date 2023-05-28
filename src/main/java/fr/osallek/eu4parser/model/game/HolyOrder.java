package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class HolyOrder {

    private final ClausewitzItem item;

    public HolyOrder(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<String> getIcon() {
        return this.item.getVarAsString("icon");
    }

    public void setIcon(String icon) {
        if (StringUtils.isBlank(icon)) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public Power getCategory() {
        return Power.byName(this.item.getVarAsString("cost_type").get().replace("_power", ""));
    }

    public void setCategory(Power category) {
        this.item.setVariable("cost_type", category.name().toLowerCase() + "_power");
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HolyOrder holyOrder)) {
            return false;
        }

        return Objects.equals(getName(), holyOrder.getName());
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
