package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class Opinion {

    private final ClausewitzItem item;

    public Opinion(ClausewitzItem item) {
        this.item = item;
    }

    public String getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        ClausewitzVariable nameVar = this.item.getVar("date");

        if (nameVar != null) {
            nameVar.setValue(date);
        } else {
            this.item.addVariable("date", date);
        }
    }

    public Double getCurrentOpinion() {
        return this.item.getVarAsDouble("current_opinion");
    }

    public void setCurrentOpinion(Double currentOpinion) {
        ClausewitzVariable var = this.item.getVar("current_opinion");

        if (var != null) {
            var.setValue(currentOpinion);
        } else {
            this.item.addVariable("current_opinion", currentOpinion);
        }
    }

    public Boolean getExpiryDate() {
        return this.item.getVarAsBool("expiry_date");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String modifier, Date date, Double currentOpinion) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "opinion", parent.getOrder() + 1);
        toItem.addVariable("modifier", modifier);
        toItem.addVariable("current_opinion", currentOpinion);

        if (date != null) {
            toItem.addVariable("date", date);
            toItem.addVariable("expiry_date", true);
        }

        parent.addChild(toItem);

        return toItem;
    }
}
