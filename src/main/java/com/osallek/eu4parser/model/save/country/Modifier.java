package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.GameModifier;
import com.osallek.eu4parser.model.game.Modifiers;
import com.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;
import java.util.Date;

public class Modifier {

    private final Game game;

    private final ClausewitzItem item;

    public Modifier(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public String getModifierName() {
        return this.item.getVarAsString("modifier");
    }

    public GameModifier getModifier() {
        return this.game.getModifier(this.item.getVarAsString("modifier"));
    }

    public Modifiers getModifiers(Country country) {
        GameModifier gameModifier = getModifier();
        return gameModifier == null ? null : gameModifier.getModifiers(country);
    }

    public Modifiers getModifiers(SaveProvince province) {
        GameModifier gameModifier = getModifier();
        return gameModifier == null ? null : gameModifier.getModifiers(province);
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
