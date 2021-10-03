package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    public Condition getCanBuyIdea() {
        ClausewitzItem child = item.getChild("can_buy_idea");
        return child == null ? null : new Condition(child);
    }

    public void setCanBuyIdea(Condition condition) {
        if (condition == null) {
            this.item.removeChild("can_buy_idea");
            return;
        }

        ClausewitzItem child = this.item.getChild("can_buy_idea");
        //Todo Condition => item
    }

    public Condition getTrigger() {
        ClausewitzItem child = item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public void setTrigger(Condition condition) {
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
                        .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ReligiousReforms)) {
            return false;
        }

        ReligiousReforms religiousReforms = (ReligiousReforms) o;

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
