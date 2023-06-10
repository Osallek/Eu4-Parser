package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.ParliamentIssue;

import java.time.LocalDate;
import java.util.Optional;

public record ActiveParliamentIssue(ClausewitzItem item, Game game) {

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public Optional<Integer> getBack() {
        return this.item.getVarAsInt("back");
    }

    public Optional<ParliamentIssue> getWhich() {
        return this.item.getVarAsString("which").map(this.game::getParliamentIssue);
    }

    public void setWhich(ParliamentIssue which) {
        this.item.setVariable("which", ClausewitzUtils.addQuotes(which.getName()));
    }
}
