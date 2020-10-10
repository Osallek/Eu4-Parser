package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class EstateModifier {

    private final String desc;

    private final Condition trigger;

    private final double amount;

    public EstateModifier(ClausewitzItem item, String modifierName) {
        this.desc = item.getVarAsString("desc");

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

        this.amount = item.getVarAsDouble(modifierName);
    }

    public String getDesc() {
        return desc;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EstateModifier)) {
            return false;
        }

        EstateModifier names = (EstateModifier) o;

        return Objects.equals(desc, names.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desc);
    }

    @Override
    public String toString() {
        return desc;
    }
}
