package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Hegemon;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.save.Save;

public record SaveHegemon(ClausewitzItem item, Save save, Hegemon hegemon) {

    public SaveCountry getCountry() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("country")));
    }

    public void setCountry(SaveCountry country) {
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
        return ModifiersUtils.sumModifiers(hegemon().getBase(),
                                           ModifiersUtils.scaleModifiers(hegemon().getScale(), getProgress() / 100),
                                           getProgress() >= 100d ? hegemon().getMax() : null);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, SaveCountry country, double progress, int order) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, order, true, true);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("progress", progress);

        return toItem;
    }
}
