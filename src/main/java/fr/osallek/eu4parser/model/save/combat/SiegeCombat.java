package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.save.Save;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

public class SiegeCombat extends Combat<SiegeCombatant> {

    public SiegeCombat(ClausewitzItem item, Save save) {
        super(item, save, SiegeCombatant::new);
    }

    public Optional<Double> getMorale() {
        return this.item.getVarAsDouble("morale");
    }

    public void setMorale(double morale) {
        this.item.setVariable("morale", morale);
    }

    public Optional<Integer> getBreach() {
        return this.item.getVarAsInt("breach");
    }

    public void setBreach(int breach) {
        if (breach < 0) {
            breach = 0;
        }

        this.item.setVariable("breach", breach);
    }

    public Optional<Integer> getRoll() {
        return this.item.getVarAsInt("roll");
    }

    public void setRoll(int roll) {
        if (roll < 0) {
            roll = 0;
        }

        this.item.setVariable("roll", roll);
    }

    public Optional<Integer> getTotal() {
        return this.item.getVarAsInt("total");
    }

    public void setTotal(int total) {
        if (total < 0) {
            total = 0;
        }

        this.item.setVariable("total", total);
    }

    public Optional<LocalDate> getLastAssault() {
        return this.item.getVarAsDate("last_assault").filter(Predicate.not(Eu4Utils.DEFAULT_DATE::equals));
    }

    public void setLastAssault(LocalDate lastAssault) {
        this.item.setVariable("last_assault", lastAssault);
    }

    public Optional<Integer> getLastImpact() {
        return this.item.getVarAsInt("last_impact");
    }

    public Optional<Integer> getImpact() {
        return this.item.getVarAsInt("impact");
    }

    public boolean active() {
        return this.item.getVarAsBool("active").map(BooleanUtils::toBoolean).orElse(false);
    }
}
