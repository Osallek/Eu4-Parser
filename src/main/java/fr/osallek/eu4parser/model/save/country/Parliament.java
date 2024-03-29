package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.ParliamentIssue;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;

public class Parliament {

    private final Game game;

    private final ClausewitzItem item;

    public Parliament(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public LocalDate getLastDebate() {
        return this.item.getVarAsDate("last_debate");
    }

    public void setLastDebate(LocalDate lastDebate) {
        this.item.setVariable("last_debate", lastDebate);
    }

    public boolean getRecalculateIssues() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("recalculate_issues"));
    }

    public ParliamentIssue getEnactedParliamentIssue() {
        return this.game.getParliamentIssue(ClausewitzUtils.removeQuotes(this.item.getVarAsString("enacted_parliament_issue")));
    }

    public void setEnactedParliamentIssue(ParliamentIssue enactedParliamentIssue) {
        this.item.setVariable("enacted_parliament_issue", ClausewitzUtils.addQuotes(enactedParliamentIssue.getName()));
    }

    public ActiveParliamentIssue getActiveParliamentIssue() {
        ClausewitzItem activeParliamentIssueItem = this.item.getChild("active_parliament_issue");

        return activeParliamentIssueItem != null ? new ActiveParliamentIssue(activeParliamentIssueItem, this.game) : null;
    }
}
