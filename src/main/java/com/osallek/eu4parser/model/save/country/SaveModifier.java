package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.Modifier;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.GameModifier;
import com.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;

public class SaveModifier {

    private final Game game;

    private final ClausewitzItem item;

    public SaveModifier(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public String getModifierName() {
        return this.item.getVarAsString("modifier");
    }

    public GameModifier getModifier() {
        return this.game.getModifier(this.item.getVarAsString("modifier"));
    }

    public Double getModifiers(Country country, Modifier modifier) {
        GameModifier gameModifier = getModifier();
        return gameModifier == null ? null : gameModifier.getModifier(country, modifier);
    }

    public Double getModifiers(SaveProvince province, Modifier modifier) {
        GameModifier gameModifier = getModifier();
        return gameModifier == null ? null : gameModifier.getModifier(province, modifier);
    }

    public Boolean getHidden() {
        return this.item.getVarAsBool("hidden");
    }

    public Boolean isParliamentModifier() {
        return this.item.getVarAsBool("parliament_modifier");
    }

    public Boolean rulerModifier() {
        return this.item.getVarAsBool("ruler_modifier");
    }

    public LocalDate getDate() {
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

    public Boolean isPermanent() {
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
