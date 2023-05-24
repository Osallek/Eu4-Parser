package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CasusBelli {

    private final ClausewitzItem item;

    public CasusBelli(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<Boolean> isValidForSubject() {
        return this.item.getVarAsBool("valid_for_subject");
    }

    public void setValidForSubject(Boolean validForSubject) {
        if (validForSubject == null) {
            this.item.removeVariable("valid_for_subject");
        } else {
            this.item.setVariable("valid_for_subject", validForSubject);
        }
    }

    public Optional<Boolean> isTriggeredOnly() {
        return this.item.getVarAsBool("is_triggered_only");
    }

    public void setIsTriggeredOnly(Boolean isTriggeredOnly) {
        if (isTriggeredOnly == null) {
            this.item.removeVariable("is_triggered_only");
        } else {
            this.item.setVariable("is_triggered_only", isTriggeredOnly);
        }
    }

    public Optional<Boolean> isExclusive() {
        return this.item.getVarAsBool("exclusive");
    }

    public void setExclusive(Boolean exclusive) {
        if (exclusive == null) {
            this.item.removeVariable("exclusive");
        } else {
            this.item.setVariable("exclusive", exclusive);
        }
    }

    public Optional<Boolean> isIndependence() {
        return this.item.getVarAsBool("independence");
    }

    public void setIndependence(Boolean independence) {
        if (independence == null) {
            this.item.removeVariable("independence");
        } else {
            this.item.setVariable("independence", independence);
        }
    }

    public Optional<Boolean> isNoOpinionHit() {
        return this.item.getVarAsBool("no_opinion_hit");
    }

    public void setNoOpinionHit(Boolean noOpinionHit) {
        if (noOpinionHit == null) {
            this.item.removeVariable("no_opinion_hit");
        } else {
            this.item.setVariable("no_opinion_hit", noOpinionHit);
        }
    }

    public Optional<Boolean> isCoalition() {
        return this.item.getVarAsBool("coalition");
    }

    public void setCoalition(Boolean coalition) {
        if (coalition == null) {
            this.item.removeVariable("coalition");
        } else {
            this.item.setVariable("coalition", coalition);
        }
    }

    public Optional<Boolean> supportRebels() {
        return this.item.getVarAsBool("support_rebels");
    }

    public void setSupportRebels(Boolean supportRebels) {
        if (supportRebels == null) {
            this.item.removeVariable("support_rebels");
        } else {
            this.item.setVariable("support_rebels", supportRebels);
        }
    }

    public Optional<Boolean> isLeague() {
        return this.item.getVarAsBool("league");
    }

    public void setLeague(Boolean league) {
        if (league == null) {
            this.item.removeVariable("league");
        } else {
            this.item.setVariable("league", league);
        }
    }

    public Optional<Boolean> isCallEmpireMembers() {
        return this.item.getVarAsBool("call_empire_members");
    }

    public void setCallEmpireMembers(Boolean callEmpireMembers) {
        if (callEmpireMembers == null) {
            this.item.removeVariable("call_empire_members");
        } else {
            this.item.setVariable("call_empire_members", callEmpireMembers);
        }
    }

    public Optional<Integer> getAiPeaceDesire() {
        return this.item.getVarAsInt("ai_peace_desire");
    }

    public void setAiPeaceDesire(Integer aiPeaceDesire) {
        if (aiPeaceDesire == null) {
            this.item.removeVariable("ai_peace_desire");
        } else {
            this.item.setVariable("ai_peace_desire", aiPeaceDesire);
        }
    }

    public Optional<Integer> getMonths() {
        return this.item.getVarAsInt("months");
    }

    public void setMonths(Integer months) {
        if (months == null) {
            this.item.removeVariable("months");
        } else {
            this.item.setVariable("months", months);
        }
    }

    public Optional<String> getWarGoal() {
        return this.item.getVarAsString("war_goal");
    }

    public void setWarGoal(Integer warGoal) {
        if (warGoal == null) {
            this.item.removeVariable("war_goal");
        } else {
            this.item.setVariable("war_goal", warGoal);
        }
    }

    public Optional<ConditionAnd> getPrerequisites() {
        return this.item.getChild("prerequisites").map(ConditionAnd::new);
    }

    public List<String> getAttackerDisabledPo() {
        return this.item.getList("attacker_disabled_po").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setAttackerDisabledPo(List<String> attackerDisabledPo) {
        if (CollectionUtils.isEmpty(attackerDisabledPo)) {
            this.item.removeList("attacker_disabled_po");
            return;
        }

        this.item.getList("manufactory")
                 .ifPresentOrElse(list -> list.setAll(attackerDisabledPo.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("attacker_disabled_po", attackerDisabledPo.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CasusBelli)) {
            return false;
        }

        CasusBelli casusBelli = (CasusBelli) o;

        return Objects.equals(getName(), casusBelli.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
