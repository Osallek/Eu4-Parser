package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Objects;

public class StaticModifier extends GameModifier {

    private final StaticModifiers staticModifiers;

    public StaticModifier(ClausewitzItem item) {
        super(item);
        this.staticModifiers = StaticModifiers.value(getName());
    }

    @Override
    public Modifiers getModifiers(Country country) {
        return this.staticModifiers.applyToCountry.apply(country, this.staticModifiers);
    }

    @Override
    public Modifiers getModifiers(SaveProvince province) {
        return this.staticModifiers.applyToProvince.apply(province, this.staticModifiers);
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
