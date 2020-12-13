package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Rival {

    private final Save save;

    private final ClausewitzItem item;

    public Rival(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public String getRivalTag() {
        return this.item.getVarAsString("country");
    }

    public Country getRival() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(getRivalTag()));
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Country country, LocalDate date, int order) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", order);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("date", date);

        parent.addChild(toItem, true);

        return toItem;
    }
}
