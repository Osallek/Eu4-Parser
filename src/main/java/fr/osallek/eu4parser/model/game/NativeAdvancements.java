package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        ClausewitzVariable variable = item.getVar("category");
        return variable == null ? null : Power.byName(variable.getValue());
    }

    public void setCategory(Power power) {
        this.item.setVariable("category", power.name());
    }

    public List<NativeAdvancement> getNativeAdvancements() {
        AtomicInteger i = new AtomicInteger();
        return this.item.getChildrenNot("ai_will_do")
                        .stream()
                        .map(child -> new NativeAdvancement(child, this, i.getAndIncrement()))
                        .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NativeAdvancements)) {
            return false;
        }

        NativeAdvancements nativeAdvancements = (NativeAdvancements) o;

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
