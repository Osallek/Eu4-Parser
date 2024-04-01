package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public record Rival(ClausewitzItem item, Save save) {

    public String getRivalTag() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("country"));
    }

    public SaveCountry getRival() {
        return this.save.getCountry(getRivalTag());
    }

    public LocalDate getDate() {
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
