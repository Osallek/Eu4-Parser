package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Optional;

public record SavedEventTarget(ClausewitzItem item, ActiveAgendaScope activeAgendaScope) {

    public Optional<SaveProvince> getProvince() {
        return this.item.getVarAsInt("province").map(i -> this.activeAgendaScope.getActiveAgenda().country().getSave().getProvince(i));
    }

    public void setProvince(SaveProvince province) {
        this.item.setVariable("province", province.getId());
    }

    public Optional<SaveCountry> getCountry() {
        return this.item.getVarAsString("country").map(s -> this.activeAgendaScope.getActiveAgenda().country().getSave().getCountry(s));
    }

    public void setCountry(SaveCountry country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }
}
