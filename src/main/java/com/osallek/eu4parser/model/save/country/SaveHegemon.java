package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.model.game.Hegemon;
import com.osallek.eu4parser.model.game.Modifiers;
import com.osallek.eu4parser.model.save.Save;

public class SaveHegemon {

    private final Save save;

    private final ClausewitzItem item;

    private final Hegemon hegemon;

    public SaveHegemon(ClausewitzItem item, Save save, Hegemon hegemon) {
        this.save = save;
        this.item = item;
        this.hegemon = hegemon;
    }

    public Hegemon getHegemon() {
        return hegemon;
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

    public Modifiers getModifiers() {
        return ModifiersUtils.sumModifiers(getHegemon().getBase(),
                                           ModifiersUtils.scaleModifiers(getHegemon().getScale(), getProgress() / 100),
                                           getProgress() >= 100d ? getHegemon().getMax() : null);
    }
}
