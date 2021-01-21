package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class ReligiousReform {

    private final String name;

    private String localizedName;

    private final ReligiousReforms nativeAdvancements;

    private final int index;

    private final Modifiers modifiers;

    public ReligiousReform(ClausewitzItem item, ReligiousReforms religiousReforms, int index) {
        this.name = item.getName();
        this.nativeAdvancements = religiousReforms;
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

    public ReligiousReforms getNativeAdvancements() {
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

        if (!(o instanceof ReligiousReform)) {
            return false;
        }

        ReligiousReform that = (ReligiousReform) o;
        return Objects.equals(name, that.name);
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
