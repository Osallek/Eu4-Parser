package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class ActiveAgenda {

    private final Country country;

    private final ClausewitzItem item;

    public ActiveAgenda(ClausewitzItem item, Country country) {
        this.country = country;
        this.item = item;
    }

    public String getAgenda() {
        return this.item.getVarAsString("agenda");
    }

    public void setAgenda(String agenda) {
        this.item.setVariable("agenda", ClausewitzUtils.addQuotes(agenda));
    }

    public Estate getEstate() {
        return this.country.getEstate(ClausewitzUtils.removeQuotes(this.item.getVarAsString("estate")));
    }

    public void setEstate(Estate estate) {
        this.item.setVariable("estate", ClausewitzUtils.addQuotes(estate.getType()));
    }

    public Date getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(Date expiryDate) {
        this.item.setVariable("expiry_date", expiryDate);
    }

    public Country getCountry() {
        return country;
    }
}
