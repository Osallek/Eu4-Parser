package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;

import java.util.function.Function;

public abstract class Combat<C extends Combatant> {

    protected final ClausewitzItem item;

    private final Function<ClausewitzItem, C> supplier;

    protected Id id;

    protected C attacker;

    protected C defender;

    public Combat(ClausewitzItem item, Function<ClausewitzItem, C> supplier) {
        this.item = item;
        this.supplier = supplier;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public Integer getPhase() {
        return this.item.getVarAsInt("phase");
    }

    public Integer getDay() {
        return this.item.getVarAsInt("day");
    }

    public void setDay(int day) {
        if (day < 0) {
            day = 0;
        }

        this.item.setVariable("day", day);
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public C getAttacker() {
        return attacker;
    }

    public C getDefender() {
        return defender;
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }

        ClausewitzItem attackerItem = this.item.getChild("attacker");

        if (attackerItem != null) {
            this.attacker = supplier.apply(attackerItem);
        }

        ClausewitzItem defenderItem = this.item.getChild("defender");

        if (defenderItem != null) {
            this.defender = supplier.apply(defenderItem);
        }
    }
}
