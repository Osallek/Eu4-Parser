package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;
import java.util.Optional;

public record SupplyDepot(ClausewitzItem item, Save save) {

    public Optional<SaveProvince> getProvince() {
        return this.item.getVarAsInt("province").map(this.save::getProvince);
    }

    public Optional<SaveCountry> getBuilder() {
        return this.item.getVarAsString("builder").map(this.save::getCountry);
    }

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country, LocalDate date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "rival", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
