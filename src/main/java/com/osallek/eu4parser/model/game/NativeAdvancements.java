package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NativeAdvancements {

    private final String name;

    private String localizedName;

    private final Power category;

    private final List<NativeAdvancement> nativeAdvancements;

    public NativeAdvancements(ClausewitzItem item) {
        this.name = item.getName();
        this.category = Power.valueOf(item.getVarAsString("category").toUpperCase());

        AtomicInteger i = new AtomicInteger();
        this.nativeAdvancements = item.getChildrenNot("ai_will_do")
                                      .stream()
                                      .map(child -> new NativeAdvancement(child, this, i.getAndIncrement()))
                                      .collect(Collectors.toList());
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

    public Power getCategory() {
        return category;
    }

    public List<NativeAdvancement> getNativeAdvancements() {
        return nativeAdvancements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof NativeAdvancements)) {
            return false;
        }

        NativeAdvancements that = (NativeAdvancements) o;
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
