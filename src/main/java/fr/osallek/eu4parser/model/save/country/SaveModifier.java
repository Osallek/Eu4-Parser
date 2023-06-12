package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.GameModifier;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;
import java.util.Optional;

public class SaveModifier {

    private final Game game;

    private final ClausewitzItem item;

    public SaveModifier(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Optional<String> getModifierName() {
        return this.item.getVarAsString("modifier");
    }

    public Optional<GameModifier> getModifier() {
        return getModifierName().map(this.game::getModifier);
    }

    public Optional<Double> getModifiers(SaveCountry country, Modifier modifier) {
        return getModifier().flatMap(gameModifier -> gameModifier.getModifier(country, modifier));
    }

    public Optional<Double> getModifiers(SaveProvince province, Modifier modifier) {
        return getModifier().flatMap(gameModifier -> gameModifier.getModifier(province, modifier));
    }

    public Optional<Boolean> getHidden() {
        return this.item.getVarAsBool("hidden");
    }

    public Optional<Boolean> isParliamentModifier() {
        return this.item.getVarAsBool("parliament_modifier");
    }

    public Optional<Boolean> rulerModifier() {
        return this.item.getVarAsBool("ruler_modifier");
    }

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    /**
     * @param date If null set to never expires
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            this.item.setVariable("date", "-1.1.1");
            this.item.setVariable("permanent", true);
        } else {
            this.item.setVariable("date", date);
        }
    }

    public Optional<Boolean> isPermanent() {
        return this.item.getVarAsBool("permanent");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String modifier, LocalDate date, Boolean hidden) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "modifier", parent.getOrder() + 1);
        toItem.addVariable("modifier", ClausewitzUtils.addQuotes(modifier));
        toItem.addVariable("hidden", hidden);

        if (date == null) {
            toItem.addVariable("date", "-1.1.1");
            toItem.addVariable("permanent", true);
        } else {
            toItem.addVariable("date", date);
        }

        parent.addChild(toItem);

        return toItem;
    }
}
