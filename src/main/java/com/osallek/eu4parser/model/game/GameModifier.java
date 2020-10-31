package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.province.SaveProvince;

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

    public Modifiers getModifiers() {
        return modifiers;
    }

    public Modifiers getModifiers(Country country) {
        return modifiers;
    }

    public Modifiers getModifiers(SaveProvince province) {
        return modifiers;
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
