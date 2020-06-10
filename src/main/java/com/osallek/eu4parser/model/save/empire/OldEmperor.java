package com.osallek.eu4parser.model.save.empire;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class OldEmperor {

    private final ClausewitzItem item;

    public OldEmperor(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getId() {
        ClausewitzVariable idVar = this.item.getVar("id");

        if (idVar != null) {
            return idVar.getAsInt();
        } else {
            return null;
        }
    }

    public String getCountry() {
        ClausewitzVariable countryVar = this.item.getVar("country");

        if (countryVar != null) {
            return countryVar.getValue();
        } else {
            return null;
        }
    }

    public Date getDate() {
        ClausewitzVariable dateVar = this.item.getVar("date");

        if (dateVar != null) {
            return dateVar.getAsDate();
        } else {
            return null;
        }
    }

    public void setCountry(String tag) {
        ClausewitzVariable countryVar = this.item.getVar("country");

        if (countryVar != null) {
            countryVar.setValue(ClausewitzUtils.hasQuotes(tag) ? tag : "\"" + tag.toUpperCase() + "\"");
        }
    }

    public void setDate(Date date) {
        ClausewitzVariable dateVar = this.item.getVar("date");

        if (dateVar != null) {
            dateVar.setValue(date);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String id, String country, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "old_emperor", parent.getOrder() + 1);
        toItem.addVariable("id", id);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
