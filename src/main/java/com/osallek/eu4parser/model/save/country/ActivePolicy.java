package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.Policy;

import java.time.LocalDate;

public class ActivePolicy {

    private final Game game;

    private final ClausewitzItem item;

    public ActivePolicy(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Policy getPolicy() {
        return this.game.getPolicy(ClausewitzUtils.removeQuotes(this.item.getVarAsString("policy")));
    }

    public void setPolicy(Policy policy) {
        LocalDate date = this.item.getVarAsDate("date");

        if (date != null) {
            this.item.setVariable("policy", ClausewitzUtils.addQuotes(policy.getName()));
        }
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Policy policy, LocalDate date, int order) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "active_policy", order);
        toItem.addVariable("policy", ClausewitzUtils.addQuotes(policy.getName()));
        toItem.addVariable("date", date);

        parent.addChild(toItem, true);

        return toItem;
    }
}
