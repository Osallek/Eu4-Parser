package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class VictoryCard {

    private final ClausewitzItem item;

    public VictoryCard(ClausewitzItem item) {
        this.item = item;
    }

    public String getArea() {
        return this.item.getVarAsString("area");
    }

    public void setArea(String area) {
        ClausewitzVariable var = this.item.getVar("area");
        area = ClausewitzUtils.addQuotes(area);

        if (var != null) {
            var.setValue(area);
        } else {
            this.item.addVariable("area", area);
        }
    }

    public Integer getLevel() {
        return this.item.getVarAsInt("level");
    }

    public void setLevel(Integer level) {
        ClausewitzVariable var = this.item.getVar("level");

        if (var != null) {
            var.setValue(level);
        } else {
            this.item.addVariable("level", level);
        }
    }

    public Double getScore() {
        return this.item.getVarAsDouble("score");
    }

    public void setScore(Double score) {
        ClausewitzVariable var = this.item.getVar("score");

        if (var != null) {
            var.setValue(score);
        } else {
            this.item.addVariable("score", score);
        }
    }

    public Boolean wasFulfilled() {
        return this.item.getVarAsBool("was_fulfilled");
    }

    public void setWasFulfilled(boolean wasFulfilled) {
        ClausewitzVariable var = this.item.getVar("was_fulfilled");

        if (var != null) {
            var.setValue(wasFulfilled);
        } else {
            this.item.addVariable("was_fulfilled", wasFulfilled);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String area, Integer level, Double score, boolean wasFulfilled) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "victory_card", parent.getOrder() + 1);
        toItem.addVariable("area", ClausewitzUtils.addQuotes(area));
        toItem.addVariable("level", level);
        toItem.addVariable("score", score);
        toItem.addVariable("was_fulfilled", wasFulfilled);

        parent.addChild(toItem);

        return toItem;
    }
}
