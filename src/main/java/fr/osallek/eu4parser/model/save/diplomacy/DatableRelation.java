package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.Optional;

public class DatableRelation {

    protected final Save save;

    protected final ClausewitzItem item;

    public DatableRelation(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Optional<SaveCountry> getFirst() {
        return this.item.getVarAsString("first").map(this.save::getCountry);
    }

    public void setFirst(SaveCountry country) {
        this.item.setVariable("first", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Optional<SaveCountry> getSecond() {
        return this.item.getVarAsString("second").map(this.save::getCountry);
    }

    public void setSecond(SaveCountry country) {
        this.item.setVariable("second", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Optional<LocalDate> getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        this.item.setVariable("start_date", startDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, LocalDate startDate) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, parent.getOrder() + 1);
        toItem.addVariable("first", ClausewitzUtils.addQuotes(first));
        toItem.addVariable("second", ClausewitzUtils.addQuotes(second));
        toItem.addVariable("start_date", startDate);

        parent.addChild(toItem);

        return toItem;
    }
}
