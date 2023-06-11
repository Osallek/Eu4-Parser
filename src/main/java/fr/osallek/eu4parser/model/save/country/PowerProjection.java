package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record PowerProjection(ClausewitzItem item) {

    public Optional<String> getTarget() {
        return this.item.getVarAsString("target");
    }

    public Optional<String> getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public Optional<Double> getCurrent() {
        return this.item.getVarAsDouble("current");
    }

    public void setCurrent(Double current) {
        this.item.setVariable("current", current);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String target, String modifier, Double current) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "power_projection", parent.getOrder() + 1);
        toItem.addVariable("target", target);
        toItem.addVariable("modifier", modifier);
        toItem.addVariable("current", current);

        parent.addChild(toItem);

        return toItem;
    }
}
