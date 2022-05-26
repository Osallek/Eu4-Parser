package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

public abstract class Combatant {

    protected final Save save;

    protected final ClausewitzItem item;

    protected Id unit;

    protected Combatant(ClausewitzItem item, Save save) {
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

    public List<SaveCountry> getParticipatingCountries() {
        return this.item.getVarsAsStrings("participating_country").stream().map(this.save::getCountry).toList();
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
