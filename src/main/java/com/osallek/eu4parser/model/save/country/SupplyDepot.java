package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Date;

public class SupplyDepot {

    private final Save save;

    private final ClausewitzItem item;

    public SupplyDepot(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public SaveProvince getProvince() {
        return this.save.getProvince(this.item.getVarAsInt("province"));
    }

    public Country getBuilder() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("builder")));
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Country country, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
