package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

public class Hegemon {

    private final Save save;

    private final ClausewitzItem item;

    public Hegemon(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public Country getCountry() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("country")));
    }

    public void setCountry(Country country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Double getProgress() {
        return this.item.getVarAsDouble("progress");
    }

    public void setProgress(Double progress) {
        progress = Math.max(0d, Math.min(progress, 100d));
        this.item.setVariable("progress", progress);
    }
}
