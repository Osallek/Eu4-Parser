package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.Date;

public class CountryState {

    private final Save save;

    private final ClausewitzItem item;

    private Edit activeEdit;

    public CountryState(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public Double getProsperity() {
        return this.item.getVarAsDouble("prosperity");
    }

    public void setProsperity(double prosperity) {
        this.item.setVariable("prosperity", prosperity);
    }

    public Boolean hasStatePatriarch() {
        return this.item.getVarAsBool("has_state_patriach");
    }

    public void setHasStatePatriarch(boolean hasStatePatriach) {
        this.item.setVariable("has_state_patriach", hasStatePatriach);
    }

    public Country getCountry() {
        return this.save.getCountry(this.item.getVarAsString("country"));
    }

    public Edit getActiveEdit() {
        return activeEdit;
    }

    public void setActiveEdit(String which, Date date) {
        if (this.activeEdit != null) {
            this.activeEdit.setWhich(which);
            this.activeEdit.setDate(date);
        } else {
            Edit.addToItem(this.item, which, date);
            refreshAttributes();
        }
    }

    public void removeActiveEdit() {
        this.item.removeChild("active_edict");
        this.activeEdit = null;
    }

    private void refreshAttributes() {
        ClausewitzItem activeEditItem = this.item.getChild("active_edict");

        if (activeEditItem != null) {
            this.activeEdit = new Edit(activeEditItem);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Country country) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "country_state", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("prosperity", 0d);

        parent.addChild(toItem);

        return toItem;
    }
}
