package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class ReligiousReform {

    private final ClausewitzItem item;

    private String localizedName;

    private final ReligiousReforms nativeAdvancements;

    private final int index;

    public ReligiousReform(ClausewitzItem item, ReligiousReforms religiousReforms, int index) {
        this.item = item;
        this.nativeAdvancements = religiousReforms;
        this.index = index;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
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
        return new Modifiers(this.item.getVariables());
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
