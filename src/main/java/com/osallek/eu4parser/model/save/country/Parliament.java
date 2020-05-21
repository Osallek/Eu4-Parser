package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class Parliament {

    private final ClausewitzItem item;

    private ActiveParliamentIssue activeParliamentIssue;

    public Parliament(ClausewitzItem item) {
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
        return this.item.getVarAsBool("recalculate_issues");
    }

    public String getEnactedParliamentIssue() {
        return this.item.getVarAsString("enacted_parliament_issue");
    }

    public void setEnactedParliamentIssue(String enactedParliamentIssue) {
        this.item.setVariable("enacted_parliament_issue", enactedParliamentIssue);
    }

    public ActiveParliamentIssue getActiveParliamentIssue() {
        return activeParliamentIssue;
    }

    private void refreshAttributes() {
        ClausewitzItem activeParliamentIssueItem = this.item.getChild("active_parliament_issue");

        if (activeParliamentIssueItem != null) {
            this.activeParliamentIssue = new ActiveParliamentIssue(activeParliamentIssueItem);
        }
    }
}
