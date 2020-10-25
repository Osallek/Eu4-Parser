package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.ParliamentIssue;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;

public class Parliament {

    private final Game game;

    private final ClausewitzItem item;

    private ActiveParliamentIssue activeParliamentIssue;

    public Parliament(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
        refreshAttributes();
    }

    public Date getLastDebate() {
        return this.item.getVarAsDate("last_debate");
    }

    public void setLastDebate(Date lastDebate) {
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
        return activeParliamentIssue;
    }

    private void refreshAttributes() {
        ClausewitzItem activeParliamentIssueItem = this.item.getChild("active_parliament_issue");

        if (activeParliamentIssueItem != null) {
            this.activeParliamentIssue = new ActiveParliamentIssue(activeParliamentIssueItem, this.game);
        }
    }
}
