package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class Icon {

    private final String name;

    private final Condition allow;

    private final Modifiers modifiers;

    public Icon(ClausewitzItem item) {
        this.name = item.getName();
        this.allow = new Condition(item.getChild("allow"));
        this.modifiers = new Modifiers(item.getVariables());
    }

    public String getName() {
        return name;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }
}
