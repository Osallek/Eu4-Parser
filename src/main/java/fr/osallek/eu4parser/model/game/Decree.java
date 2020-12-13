package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Decree {
    
    private final String name;

    private String localizedName;

    private Integer cost;

    private Integer duration;

    private Modifiers modifiers;

    public Decree(ClausewitzItem item) {
        this.name = item.getName();
        this.cost = item.getVarAsInt("cost");
        this.duration = item.getVarAsInt("time");

        this.modifiers = new Modifiers(item.getChild("modifier"));
    }

    public Decree(Decree other) {
        this.name = other.name;
        this.localizedName = other.localizedName;
        this.cost = other.cost;
        this.duration = other.duration;
        this.modifiers = other.modifiers;
    }

    public Decree(String name) {
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

    public Integer getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Modifiers getModifiers() {
        return this.modifiers;
    }

    public void addModifier(String modifier, String quantity) {
        if (this.modifiers == null) {
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

        if (!(o instanceof Decree)) {
            return false;
        }
        Decree decree = (Decree) o;
        return Objects.equals(getName(), decree.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
