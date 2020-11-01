package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.country.Country;

import java.time.LocalDate;

public class DatableRelation {

    protected final Save save;

    protected final ClausewitzItem item;

    public DatableRelation(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Country getFirst() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("first")));
    }

    public Country getSecond() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("second")));
    }

    public LocalDate getStartDate() {
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
