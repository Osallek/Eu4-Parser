package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;
import java.util.Optional;

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

    public Optional<Integer> getButtonGfx() {
        return this.item.getVarAsInt("button_gfx");
    }

    public void setButtonGfx(Integer buttonGfx) {
        if (buttonGfx == null) {
            this.item.removeVariable("button_gfx");
        } else {
            this.item.setVariable("button_gfx", buttonGfx);
        }
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("can_select").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("country_modifier").map(Modifiers::new);
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
