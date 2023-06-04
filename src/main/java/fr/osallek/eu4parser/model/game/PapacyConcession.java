package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

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

    public Optional<Modifiers> getHarshModifiers() {
        return this.item.getChild("harsh").map(Modifiers::new);
    }

    public Optional<Modifiers> getConcilatoryModifiers() {
        return this.item.getChild("concilatory").map(Modifiers::new);
    }
}
