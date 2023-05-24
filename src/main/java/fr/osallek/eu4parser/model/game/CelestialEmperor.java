package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public class CelestialEmperor {

    private final ClausewitzItem item;

    public CelestialEmperor(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<String> getTag() {
        return this.item.getVarAsString("celestial_emperor");
    }

    public void setTag(Country country) {
        this.item.setVariable("celestial_emperor", country.getTag());
    }
}
