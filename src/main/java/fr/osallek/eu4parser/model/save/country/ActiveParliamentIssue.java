package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.ParliamentIssue;

import java.time.LocalDate;

public record ActiveParliamentIssue(ClausewitzItem item, Game game) {

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
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
