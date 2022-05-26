package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public record OldEmperor(ClausewitzItem item) {

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public String getCountry() {
        return this.item.getVarAsString("country");
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setCountry(String tag) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(tag.toUpperCase()));
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String id, String country, LocalDate date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "old_emperor", parent.getOrder() + 1);
        toItem.addVariable("id", id);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
