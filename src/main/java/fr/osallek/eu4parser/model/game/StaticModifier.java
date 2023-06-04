package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Objects;
import java.util.Optional;

public class StaticModifier extends GameModifier {

    public StaticModifier(ClausewitzItem item) {
        super(item);
    }

    @Override
    public Optional<Modifiers> getModifier() {
        return Optional.empty();
    }

    @Override
    public Optional<Double> getModifier(SaveCountry country, Modifier modifierName) {
        StaticModifiers staticModifiers = StaticModifiers.value(getName());
        return Optional.ofNullable(staticModifiers.modifiers.getModifier(modifierName))
                       .map(d -> staticModifiers.applyToCountry.apply(country, staticModifiers).getModifier(modifierName));
    }

    @Override
    public Optional<Double> getModifier(SaveProvince province, Modifier modifierName) {
        StaticModifiers staticModifiers = StaticModifiers.value(getName());
        return Optional.ofNullable(staticModifiers.modifiers.getModifier(modifierName))
                       .map(d -> staticModifiers.applyToProvince.apply(province, staticModifiers).getModifier(modifierName));
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
}
