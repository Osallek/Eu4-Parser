package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.ParliamentIssue;

import java.util.Date;

public class ActiveParliamentIssue {

    private final Game game;

    private final ClausewitzItem item;

    public ActiveParliamentIssue(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public Integer getBack() {
        return this.item.getVarAsInt("back");
    }

    public ParliamentIssue getWhich() {
        return this.game.getParliamentIssue(ClausewitzUtils.removeQuotes(this.item.getVarAsString("which")));
    }

    public void setWhich(ParliamentIssue which) {
        this.item.setVariable("which", ClausewitzUtils.addQuotes(which.getName()));
    }
}
