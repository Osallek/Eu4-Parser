package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

import java.util.Objects;

public class StateEdict {

    private final ClausewitzItem item;

    public StateEdict(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Condition getPotential() {
        ClausewitzItem child = item.getChild("potential");
        return child == null ? null : new Condition(child);
    }

    public void setPotential(Condition condition) {
        if (condition == null) {
            this.item.removeChild("potential");
            return;
        }

        ClausewitzItem child = this.item.getChild("potential");
        //Todo Condition => item
    }

    public Condition getAllow() {
        ClausewitzItem child = item.getChild("allow");
        return child == null ? null : new Condition(child);
    }

    public void setAllow(Condition condition) {
        if (condition == null) {
            this.item.removeChild("allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("allow");
        //Todo Condition => item
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public Color getColor() {
        ClausewitzList list = this.item.getList("color");
        return list == null ? null : new Color(list);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StateEdict)) {
            return false;
        }

        StateEdict stateEdict = (StateEdict) o;

        return Objects.equals(getName(), stateEdict.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
