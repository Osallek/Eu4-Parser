package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

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
        ClausewitzVariable policyVar = this.item.getVar("policy");
        ClausewitzVariable dateVar = this.item.getVar("date");

        if (dateVar != null) {
            if (policyVar != null) {
                policyVar.setValue(ClausewitzUtils.addQuotes(policy));
            } else {
                this.item.addVariable("policy", ClausewitzUtils.addQuotes(policy));
            }
        }
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

    public static ClausewitzItem addToItem(ClausewitzItem parent, String policy, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "active_policy", parent.getOrder() + 1);
        toItem.addVariable("policy", ClausewitzUtils.addQuotes(policy));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
