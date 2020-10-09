package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.province.SaveProvince;

public class SavedEventTarget {

    private final ActiveAgendaScope activeAgendaScope;

    private final ClausewitzItem item;

    public SavedEventTarget(ClausewitzItem item, ActiveAgendaScope activeAgendaScope) {
        this.activeAgendaScope = activeAgendaScope;
        this.item = item;
    }

    public SaveProvince getProvince() {
        return this.activeAgendaScope.getActiveAgenda().getCountry().getSave().getProvince(this.item.getVarAsInt("province"));
    }

    public void setProvince(SaveProvince province) {
        this.item.setVariable("province", province.getId());
    }

    public Country getCountry() {
        return this.activeAgendaScope.getActiveAgenda().getCountry().getSave().getCountry(this.item.getVarAsString("country"));
    }

    public void setCountry(Country country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public ActiveAgendaScope getActiveAgendaScope() {
        return activeAgendaScope;
    }
}
