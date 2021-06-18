package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.Country;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Objects;

public class GameModifier {

    protected final String name;

    protected String localizedName;

    protected final Modifiers modifiers;

    protected GameModifier(ClausewitzItem item) {
        this.name = ClausewitzUtils.removeQuotes(item.getName()).toLowerCase();
        this.modifiers = new Modifiers();
    }

    public GameModifier(ClausewitzItem item, Modifiers modifiers) {
        this.name = item.getName();
        this.modifiers = modifiers;
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

    public Modifiers getModifier() {
        return modifiers;
    }

    public Double getModifier(Country country, Modifier modifierName) {
        return modifiers.hasModifier(modifierName) ? modifiers.getModifier(modifierName) : null;
    }

    public Double getModifier(SaveProvince province, Modifier modifierName) {
        return modifiers.hasModifier(modifierName) ? modifiers.getModifier(modifierName) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GameModifier)) {
            return false;
        }

        GameModifier that = (GameModifier) o;
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
