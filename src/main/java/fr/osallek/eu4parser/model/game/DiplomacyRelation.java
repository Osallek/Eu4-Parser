package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Optional;

public class DiplomacyRelation {

    protected final Game game;

    protected final ClausewitzItem item;

    public DiplomacyRelation(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Optional<Country> getFirst() {
        return this.item.getVarAsString("first").map(this.game::getCountry);
    }

    public void setFirst(Country country) {
        this.item.setVariable("first", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Optional<Country> getSecond() {
        return this.item.getVarAsString("second").map(this.game::getCountry);
    }

    public void setSecond(Country country) {
        this.item.setVariable("second", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Optional<LocalDate> getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        this.item.setVariable("start_date", startDate);
    }

    public Optional<LocalDate> getEndDate() {
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
