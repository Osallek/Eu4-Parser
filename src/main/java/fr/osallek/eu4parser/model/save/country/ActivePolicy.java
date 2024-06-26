package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Policy;

import java.time.LocalDate;

public record ActivePolicy(ClausewitzItem item, Game game) {

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
