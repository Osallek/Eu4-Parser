package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NativeAdvancement {

    private final String name;

    private String localizedName;

    private final NativeAdvancements nativeAdvancements;

    private final int index;

    private final Map<String, List<String>> modifiers;

    public NativeAdvancement(ClausewitzItem item, NativeAdvancements nativeAdvancements, int index) {
        this.name = item.getName();
        this.nativeAdvancements = nativeAdvancements;
        this.index = index;
        this.modifiers = item.getVariables()
                             .stream()
                             .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                            Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
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

    public Map<String, List<String>> getModifiers() {
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
