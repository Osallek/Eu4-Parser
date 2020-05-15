package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class ActiveParliamentIssue {

    private final ClausewitzItem item;

    public ActiveParliamentIssue(ClausewitzItem item) {
        this.item = item;
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public Integer getBack() {
        return this.item.getVarAsInt("back");
    }

    public String getWhich() {
        return this.item.getVarAsString("which");
    }

    public void setWhich(String which) {
        this.item.setVariable("which", which);
    }
}
