package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class NativeAdvancement {

    private final String name;

    private String localizedName;

    private final NativeAdvancements nativeAdvancements;

    private final int index;

    private final Modifiers modifiers;

    public NativeAdvancement(ClausewitzItem item, NativeAdvancements nativeAdvancements, int index) {
        this.name = item.getName();
        this.nativeAdvancements = nativeAdvancements;
        this.index = index;
        this.modifiers = new Modifiers(item.getVariables());
    }

    public String getName() {
        return this.name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public NativeAdvancements getNativeAdvancements() {
        return nativeAdvancements;
    }

    public int getIndex() {
        return index;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NativeAdvancement)) {
            return false;
        }

        NativeAdvancement that = (NativeAdvancement) o;
        return Objects.equals(getName(), that.getName());
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
