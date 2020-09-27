package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Objects;

public class CasusBelli {

    private final String name;

    private String localizedName;

    private final boolean validForSubject;

    private final boolean isTriggeredOnly;

    private final boolean exclusive;

    private final boolean independence;

    private final boolean noOpinionHit;

    private final boolean coalition;

    private final boolean supportRebels;

    private final boolean league;

    private final boolean callEmpireMembers;

    private final Integer aiPeaceDesire;

    private final Integer months;

    private final Condition prerequisites;

    private final String warGoal;

    private final List<String> attackerDisabledPo;

    public CasusBelli(ClausewitzItem item) {
        this.name = item.getName();
        this.validForSubject = BooleanUtils.toBoolean(item.getVarAsBool("valid_for_subject"));
        this.isTriggeredOnly = BooleanUtils.toBoolean(item.getVarAsBool("is_triggered_only"));
        this.exclusive = BooleanUtils.toBoolean(item.getVarAsBool("exclusive"));
        this.independence = BooleanUtils.toBoolean(item.getVarAsBool("independence"));
        this.noOpinionHit = BooleanUtils.toBoolean(item.getVarAsBool("no_opinion_hit"));
        this.coalition = BooleanUtils.toBoolean(item.getVarAsBool("coalition"));
        this.supportRebels = BooleanUtils.toBoolean(item.getVarAsBool("support_rebels"));
        this.league = BooleanUtils.toBoolean(item.getVarAsBool("league"));
        this.callEmpireMembers = BooleanUtils.toBoolean(item.getVarAsBool("call_empire_members"));
        this.aiPeaceDesire = item.getVarAsInt("ai_peace_desire");
        this.months = item.getVarAsInt("months");
        ClausewitzItem child = item.getChild("prerequisites");
        this.prerequisites = child == null ? null : new Condition(child);
        this.warGoal = item.getVarAsString("war_goal");

        ClausewitzList list = item.getList("attacker_disabled_po");
        this.attackerDisabledPo = list == null ? null : list.getValues();
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public boolean isValidForSubject() {
        return validForSubject;
    }

    public boolean isTriggeredOnly() {
        return isTriggeredOnly;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isIndependence() {
        return independence;
    }

    public boolean isNoOpinionHit() {
        return noOpinionHit;
    }

    public boolean isCoalition() {
        return coalition;
    }

    public boolean isSupportRebels() {
        return supportRebels;
    }

    public boolean isLeague() {
        return league;
    }

    public boolean isCallEmpireMembers() {
        return callEmpireMembers;
    }

    public Integer getAiPeaceDesire() {
        return aiPeaceDesire;
    }

    public Integer getMonths() {
        return months;
    }

    public Condition getPrerequisites() {
        return prerequisites;
    }

    public String getWarGoal() {
        return warGoal;
    }

    public List<String> getAttackerDisabledPo() {
        return attackerDisabledPo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CasusBelli)) {
            return false;
        }

        CasusBelli area = (CasusBelli) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
