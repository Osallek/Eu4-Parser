package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class NativeAdvancement {

    private final ClausewitzItem item;

    private final NativeAdvancements nativeAdvancements;

    private final int index;

    public NativeAdvancement(ClausewitzItem item, NativeAdvancements nativeAdvancements, int index) {
        this.item = item;
        this.index = index;
        this.nativeAdvancements = nativeAdvancements;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public NativeAdvancements getNativeAdvancements() {
        return nativeAdvancements;
    }

    public int getIndex() {
        return index;
    }

    public Modifiers getModifiers() {
        return new Modifiers(item.getVariables());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NativeAdvancement)) {
            return false;
        }

        NativeAdvancement nativeAdvancement = (NativeAdvancement) o;

        return Objects.equals(getName(), nativeAdvancement.getName());
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
