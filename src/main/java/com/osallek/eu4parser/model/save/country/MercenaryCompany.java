package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;
import java.util.Date;

public class MercenaryCompany {

    protected final ClausewitzItem item;

    protected final Country country;

    protected Id id;

    protected Id leader;

    public MercenaryCompany(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getTag() {
        return this.item.getVarAsString("tag");
    }

    public void setTag(String tag) {
        this.item.setVariable("tag", ClausewitzUtils.addQuotes(tag));
    }

    public Boolean getProgress() {
        return this.item.getVarAsBool("progress");
    }

    public Id getLeader() {
        return leader;
    }

    public void removeLeader() {
        this.item.removeChild("leader");
    }

    public Double getManpower() {
        return this.item.getVarAsDouble("manpower");
    }

    public void setManpower(Double manpower) {
        if (manpower < 0d) {
            manpower = 0d;
        }

        this.item.setVariable("manpower", manpower);
    }

    public Double getStartingManpower() {
        return this.item.getVarAsDouble("starting_manpower");
    }

    public void setStartingManpower(Double manpower) {
        if (manpower < 0d) {
            manpower = 0d;
        }

        this.item.setVariable("starting_manpower", manpower);
    }

    public LocalDate getHiringDate() {
        return this.item.getVarAsDate("hiring_date");
    }

    public LocalDate getDisbandDate() {
        return this.item.getVarAsDate("disband_date");
    }

    protected void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }

        ClausewitzItem leaderItem = this.item.getChild("leader");

        if (leaderItem != null) {
            this.leader = new Id(leaderItem);
        }
    }
}
