package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class EstateInteraction {

    private final ClausewitzItem item;

    public EstateInteraction(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getInteraction() {
        return this.item.getVarAsInt("interaction");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        ClausewitzVariable var = this.item.getVar("date");

        if (var != null) {
            var.setValue(date);
        } else {
            this.item.addVariable("date", date);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Integer interaction, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "interaction_use", parent.getOrder() + 1);
        toItem.addVariable("province_id", interaction);
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
