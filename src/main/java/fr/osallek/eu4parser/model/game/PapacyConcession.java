package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class PapacyConcession {

    private final ClausewitzItem item;

    public PapacyConcession(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Modifiers getHarshModifiers() {
        return new Modifiers(this.item.getChild("harsh"));
    }

    public Modifiers getConcilatoryModifiers() {
        return new Modifiers(this.item.getChild("concilatory"));
    }
}
