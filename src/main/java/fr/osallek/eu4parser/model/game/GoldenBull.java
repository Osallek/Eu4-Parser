package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoldenBull {

    private final String name;

    private String localizedName;

    private List<String> mechanics;

    private Modifiers modifiers;

    public GoldenBull(ClausewitzItem item) {
        this.name = item.getName();
        this.modifiers = new Modifiers(item.getChild("modifier"));

        ClausewitzList list = item.getList("mechanics");
        this.mechanics = list == null ? null : list.getValues();
    }

    public GoldenBull(GoldenBull other) {
        this.name = other.name;
        this.localizedName = other.localizedName;
        this.mechanics = other.mechanics;
        this.modifiers = other.modifiers;
    }

    public GoldenBull(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public List<String> getMechanics() {
        return this.mechanics == null ? new ArrayList<>() : this.mechanics;
    }

    public void setMechanics(List<String> mechanics) {
        this.mechanics = mechanics;
    }

    public Modifiers getModifiers() {
        return this.modifiers;
    }

    public void addModifier(String modifier, String quantity) {
        if (modifier == null) {
            this.modifiers = new Modifiers();
        }

        this.modifiers.add(modifier, quantity);
    }

    public void removeModifier(String modifier) {
        if (this.modifiers != null) {
            this.modifiers.removeModifier(modifier);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null && getName() == null) {
            return true;
        }

        if (!(o instanceof GoldenBull)) {
            return false;
        }
        GoldenBull decree = (GoldenBull) o;
        return Objects.equals(getName(), decree.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
