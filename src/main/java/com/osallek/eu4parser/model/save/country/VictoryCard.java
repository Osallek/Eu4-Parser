package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

public class VictoryCard {

    private final ClausewitzItem item;

    public VictoryCard(ClausewitzItem item) {
        this.item = item;
    }

    public String getArea() {
        return this.item.getVarAsString("area");
    }

    public void setArea(String area) {
        this.item.setVariable("area", ClausewitzUtils.addQuotes(area));
    }

    public Integer getLevel() {
        return this.item.getVarAsInt("level");
    }

    public void setLevel(Integer level) {
        this.item.setVariable("level", level);
    }

    public Double getScore() {
        return this.item.getVarAsDouble("score");
    }

    public void setScore(Double score) {
        this.item.setVariable("score", score);
    }

    public Boolean wasFulfilled() {
        return this.item.getVarAsBool("was_fulfilled");
    }

    public void setWasFulfilled(boolean wasFulfilled) {
        this.item.setVariable("was_fulfilled", wasFulfilled);
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
