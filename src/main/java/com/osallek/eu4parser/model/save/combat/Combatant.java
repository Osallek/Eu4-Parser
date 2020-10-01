package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.country.Country;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Combatant {

    protected final Save save;

    protected final ClausewitzItem item;

    protected Id unit;

    public Combatant(ClausewitzItem item, Save save) {
        this.save = save;
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
        return BooleanUtils.toBoolean(this.item.getVarAsBool("is_attacker"));
    }

    public Id getUnit() {
        return unit;
    }

    public Double getTotalLosses() {
        return this.item.getVarAsDouble("losses");
    }

    public List<Country> getParticipatingCountries() {
        return this.item.getVarsAsStrings("participating_country").stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public boolean arranged() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("arranged"));
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("unit");

        if (idChild != null) {
            this.unit = new Id(idChild);
        }
    }
}
