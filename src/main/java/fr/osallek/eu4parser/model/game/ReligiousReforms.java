package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReligiousReforms {

    private final String name;

    private String localizedName;

    private final Condition canBuyIdea;

    private final Condition trigger;

    private final List<ReligiousReform> reforms;

    public ReligiousReforms(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzItem child = item.getChild("can_buy_idea");
        this.canBuyIdea = child == null ? null : new Condition(child);

        child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

        AtomicInteger i = new AtomicInteger();
        this.reforms = item.getChildrenNot("trigger", "can_buy_idea", "ai_will_do")
                           .stream()
                           .map(reform -> new ReligiousReform(reform, this, i.getAndIncrement()))
                           .collect(Collectors.toList());

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

    public Condition getCanBuyIdea() {
        return canBuyIdea;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public List<ReligiousReform> getReforms() {
        return reforms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ReligiousReforms)) {
            return false;
        }

        ReligiousReforms area = (ReligiousReforms) o;

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
