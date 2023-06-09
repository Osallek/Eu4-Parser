package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.StateEdict;

import java.time.LocalDate;
import java.util.Optional;

public record Edict(ClausewitzItem item, Game game) {

    public Optional<StateEdict> getWhich() {
        return this.item.getVarAsString("which").map(this.game::getStateEdict);
    }

    public void setWhich(StateEdict which) {
        this.item.setVariable("which", ClausewitzUtils.addQuotes(which.getName()));
    }

    public Optional<LocalDate> getDate() {
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
