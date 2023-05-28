package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public class HreEmperor {

    private final ClausewitzItem item;

    public HreEmperor(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<String> getTag() {
        return this.item.getVarAsString("emperor");
    }

    public void setTag(Country country) {
        this.item.setVariable("emperor", country.getTag());
    }
}
