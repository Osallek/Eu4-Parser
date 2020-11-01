package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.Save;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.Date;

public class SiegeCombat extends Combat<SiegeCombatant> {

    public SiegeCombat(ClausewitzItem item, Save save) {
        super(item, save, SiegeCombatant::new);
    }

    public Double getMorale() {
        return this.item.getVarAsDouble("morale");
    }

    public void setMorale(double morale) {
        this.item.setVariable("morale", morale);
    }

    public Integer getBreach() {
        return this.item.getVarAsInt("breach");
    }

    public void setBreach(int breach) {
        if (breach < 0) {
            breach = 0;
        }

        this.item.setVariable("breach", breach);
    }

    public Integer getRoll() {
        return this.item.getVarAsInt("roll");
    }

    public void setRoll(int roll) {
        if (roll < 0) {
            roll = 0;
        }

        this.item.setVariable("roll", roll);
    }

    public Integer getTotal() {
        return this.item.getVarAsInt("total");
    }

    public void setTotal(int total) {
        if (total < 0) {
            total = 0;
        }

        this.item.setVariable("total", total);
    }

    public LocalDate getLastAssault() {
        LocalDate date = this.item.getVarAsDate("last_assault");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setLastAssault(LocalDate lastAssault) {
        this.item.setVariable("last_assault", lastAssault);
    }

    public Integer getLastImpact() {
        return this.item.getVarAsInt("last_impact");
    }

    public Integer getImpact() {
        return this.item.getVarAsInt("impact");
    }

    public boolean active() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("active"));
    }
}
