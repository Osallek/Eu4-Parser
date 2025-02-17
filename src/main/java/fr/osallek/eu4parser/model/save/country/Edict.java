package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.StateEdict;

import java.time.LocalDate;

public record Edict(ClausewitzItem item, Game game) {

    public StateEdict getWhich() {
        return this.game.getStateEdict(this.item.getVarAsString("which"));
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

        return toItem;
    }
}
