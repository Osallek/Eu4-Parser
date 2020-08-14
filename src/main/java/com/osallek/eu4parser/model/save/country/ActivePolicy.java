package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class ActivePolicy {

    private final ClausewitzItem item;

    public ActivePolicy(ClausewitzItem item) {
        this.item = item;
    }

    public String getPolicy() {
        return this.item.getVarAsString("policy");
    }

    public void setPolicy(String policy) {
        Date date = this.item.getVarAsDate("date");

        if (date != null) {
            this.item.setVariable("policy", ClausewitzUtils.addQuotes(policy));
        }
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String policy, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "active_policy", parent.getOrder() + 1);
        toItem.addVariable("policy", ClausewitzUtils.addQuotes(policy));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
