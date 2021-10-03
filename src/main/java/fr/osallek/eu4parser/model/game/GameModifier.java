package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Objects;

public abstract class GameModifier {

    protected final ClausewitzItem item;

    protected GameModifier(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public abstract Modifiers getModifier();

    public Double getModifier(SaveCountry country, Modifier modifierName) {
        return getModifier().hasModifier(modifierName) ? getModifier().getModifier(modifierName) : null;
    }

    public Double getModifier(SaveProvince province, Modifier modifierName) {
        return getModifier().hasModifier(modifierName) ? getModifier().getModifier(modifierName) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GameModifier)) {
            return false;
        }

        GameModifier gameModifier = (GameModifier) o;

        return Objects.equals(getName(), gameModifier.getName());
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
