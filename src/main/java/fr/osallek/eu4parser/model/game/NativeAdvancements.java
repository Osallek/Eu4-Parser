package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NativeAdvancements {

    private final ClausewitzItem item;

    public NativeAdvancements(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getCategory() {
        return this.item.getVar("category").map(ClausewitzVariable::getValue).map(Power::byName).get();
    }

    public void setCategory(Power power) {
        this.item.setVariable("category", power.name());
    }

    public List<NativeAdvancement> getNativeAdvancements() {
        AtomicInteger i = new AtomicInteger();
        return this.item.getChildrenNot("ai_will_do")
                        .stream()
                        .map(child -> new NativeAdvancement(child, this, i.getAndIncrement()))
                        .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NativeAdvancements nativeAdvancements)) {
            return false;
        }

        return Objects.equals(getName(), nativeAdvancements.getName());
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
