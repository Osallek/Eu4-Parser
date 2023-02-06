package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class NavalDoctrine {

    private final ClausewitzItem item;

    public NavalDoctrine(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Integer getButtonGfx() {
        return this.item.getVarAsInt("button_gfx");
    }

    public void setButtonGfx(Integer buttonGfx) {
        if (buttonGfx == null) {
            this.item.removeVariable("button_gfx");
        } else {
            this.item.setVariable("button_gfx", buttonGfx);
        }
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = this.item.getChild("can_select");
        return child == null ? null : new ConditionAnd(child);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("country_modifier"));
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NavalDoctrine navalDoctrine = (NavalDoctrine) o;

        return Objects.equals(getName(), navalDoctrine.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
