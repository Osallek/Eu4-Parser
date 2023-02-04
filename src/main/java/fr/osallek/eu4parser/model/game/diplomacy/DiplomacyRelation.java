package fr.osallek.eu4parser.model.game.diplomacy;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Country;
import fr.osallek.eu4parser.model.game.Game;

import java.time.LocalDate;

public class DiplomacyRelation {

    protected final Game game;

    protected final ClausewitzItem item;

    public DiplomacyRelation(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Country getFirst() {
        return this.game.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("first")));
    }

    public void setFirst(Country country) {
        this.item.setVariable("first", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Country getSecond() {
        return this.game.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("second")));
    }

    public void setSecond(Country country) {
        this.item.setVariable("second", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public LocalDate getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        this.item.setVariable("start_date", startDate);
    }

    public LocalDate getEndDate() {
        return this.item.getVarAsDate("end_date");
    }

    public void setEndDate(LocalDate endDate) {
        this.item.setVariable("end_date", endDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, LocalDate startDate, LocalDate endDate) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, parent.getOrder() + 1);
        toItem.addVariable("first", ClausewitzUtils.addQuotes(first));
        toItem.addVariable("second", ClausewitzUtils.addQuotes(second));
        toItem.addVariable("start_date", startDate);

        if (endDate != null) {
            toItem.addVariable("end_date", endDate);
        }

        parent.addChild(toItem);

        return toItem;
    }
}
