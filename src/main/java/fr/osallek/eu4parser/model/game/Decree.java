package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Decree {

    private final ClausewitzItem item;

    public Decree(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Integer getCost() {
        return this.item.getVarAsInt("cost");
    }

    public void setCost(Integer cost) {
        if (cost == null) {
            this.item.removeVariable("cost");
        } else {
            this.item.setVariable("cost", cost);
        }
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("time");
    }

    public void setDuration(Integer duration) {
        if (duration == null) {
            this.item.removeVariable("time");
        } else {
            this.item.setVariable("time", duration);
        }
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
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
