package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Objects;

public class StaticModifier extends GameModifier {

    private final StaticModifiers staticModifiers;

    public StaticModifier(ClausewitzItem item) {
        super(item);
        this.staticModifiers = StaticModifiers.value(getName());
    }

    @Override
    public Double getModifier(SaveCountry country, Modifier modifierName) {
        return this.staticModifiers.modifiers.hasModifier(modifierName) ? this.staticModifiers.applyToCountry.apply(country, this.staticModifiers)
                                                                                                             .getModifier(modifierName) : null;
    }

    @Override
    public Double getModifier(SaveProvince province, Modifier modifierName) {
        return this.staticModifiers.modifiers.hasModifier(modifierName) ? this.staticModifiers.applyToProvince.apply(province, this.staticModifiers)
                                                                                                              .getModifier(modifierName) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StaticModifier)) {
            return false;
        }

        StaticModifier that = (StaticModifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
