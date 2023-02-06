package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class Icon {

    private final ClausewitzItem item;

    public Icon(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getAllow() {
        return new ConditionAnd(item.getChild("allow"));
    }

    public Modifiers getModifiers() {
        return new Modifiers(item.getVariables());
    }
}
