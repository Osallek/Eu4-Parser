package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record VictoryCard(ClausewitzItem item) {

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
