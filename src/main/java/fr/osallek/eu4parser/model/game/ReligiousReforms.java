package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ReligiousReforms {

    private final ClausewitzItem item;

    public ReligiousReforms(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getCanBuyIdea() {
        ClausewitzItem child = item.getChild("can_buy_idea");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setCanBuyIdea(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("can_buy_idea");
            return;
        }

        ClausewitzItem child = this.item.getChild("can_buy_idea");
        //Todo Condition => item
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setTrigger(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("trigger");
            return;
        }

        ClausewitzItem child = this.item.getChild("trigger");
        //Todo Condition => item
    }

    public List<ReligiousReform> getReforms() {
        AtomicInteger i = new AtomicInteger();

        return this.item.getChildrenNot("trigger", "can_buy_idea", "ai_will_do")
                        .stream()
                        .map(reform -> new ReligiousReform(reform, this, i.getAndIncrement()))
                        .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ReligiousReforms religiousReforms)) {
            return false;
        }

        return Objects.equals(getName(), religiousReforms.getName());
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
