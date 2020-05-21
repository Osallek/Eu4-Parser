package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;

import java.util.List;

public abstract class Combatant {

    protected final ClausewitzItem item;

    protected Id unit;

    public Combatant(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Integer getDice() {
        return this.item.getVarAsInt("dice");
    }

    public void setDice(int dice) {
        if (dice < 0) {
            dice = 0;
        }

        this.item.setVariable("dice", dice);
    }

    public boolean isAttacker() {
        return this.item.getVarAsBool("is_attacker");
    }

    public Id getUnit() {
        return unit;
    }

    public Double getTotalLosses() {
        return this.item.getVarAsDouble("losses");
    }

    public List<String> getParticipatingCountry() {
        return this.item.getVarsAsStrings("participating_country");
    }

    public boolean arranged() {
        return this.item.getVarAsBool("arranged");
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("unit");

        if (idChild != null) {
            this.unit = new Id(idChild);
        }
    }
}
