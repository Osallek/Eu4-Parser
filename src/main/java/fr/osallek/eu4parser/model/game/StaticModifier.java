package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Objects;

public class StaticModifier extends GameModifier {

    public StaticModifier(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Modifiers getModifier() {
        return null;
    }

    @Override
    public Double getModifier(SaveCountry country, Modifier modifierName) {
        StaticModifiers staticModifiers = StaticModifiers.value(getName());
        return staticModifiers.modifiers.hasModifier(modifierName) ? staticModifiers.applyToCountry.apply(country, staticModifiers).getModifier(modifierName)
                                                                   : null;
    }

    @Override
    public Double getModifier(SaveProvince province, Modifier modifierName) {
        StaticModifiers staticModifiers = StaticModifiers.value(getName());
        return staticModifiers.modifiers.hasModifier(modifierName) ? staticModifiers.applyToProvince.apply(province, staticModifiers).getModifier(modifierName)
                                                                   : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StaticModifier staticModifier)) {
            return false;
        }

        return Objects.equals(getName(), staticModifier.getName());
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
