package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;

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

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Country country, LocalDate date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
