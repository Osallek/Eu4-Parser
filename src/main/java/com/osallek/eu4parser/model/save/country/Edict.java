package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.StateEdict;

import java.time.LocalDate;

public class Edict {

    private final Game game;

    private final ClausewitzItem item;

    public Edict(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public StateEdict getWhich() {
        return this.game.getStateEdict(ClausewitzUtils.removeQuotes(this.item.getVarAsString("which")));
    }

    public void setWhich(StateEdict which) {
        this.item.setVariable("which", ClausewitzUtils.addQuotes(which.getName()));
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, StateEdict which, LocalDate date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "active_edict", parent.getOrder() + 1);
        toItem.addVariable("which", ClausewitzUtils.addQuotes(which.getName()));
        toItem.addVariable("date", date);

        parent.addChild(toItem);

        return toItem;
    }
}
