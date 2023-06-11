package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;
import java.util.Optional;

public record Rival(ClausewitzItem item, Save save) {

    public Optional<String> getRivalTag() {
        return this.item.getVarAsString("country");
    }

    public Optional<SaveCountry> getRival() {
        return getRivalTag().map(this.save::getCountry);
    }

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country, LocalDate date, int order) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", order);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("date", date);

        parent.addChild(toItem, true);

        return toItem;
    }
}
