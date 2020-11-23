package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.country.Heir;
import com.osallek.eu4parser.model.save.country.Monarch;
import com.osallek.eu4parser.model.save.country.Queen;

import java.util.Objects;

public class RulerPersonality {

    private final String name;

    private String localizedName;

    private final Condition rulerAllow;

    private final Condition heirAllow;

    private final Condition consortAllow;

    private final Condition allow;

    private final Modifiers modifiers;

    public RulerPersonality(ClausewitzItem item) {
        this.name = item.getName();

        this.allow = item.getChild("allow") == null ? null : new Condition(item.getChild("allow"));
        this.rulerAllow = item.getChild("ruler_allow") == null ? null : new Condition(item.getChild("ruler_allow"));
        this.heirAllow = item.getChild("heir_allow") == null ? null : new Condition(item.getChild("heir_allow"));
        this.consortAllow = item.getChild("consort_allow") == null ? null : new Condition(item.getChild("consort_allow"));
        this.modifiers = new Modifiers(item);
    }

    public RulerPersonality(RulerPersonality other) {
        this.name = other.name;
        this.localizedName = other.localizedName;
        this.rulerAllow = other.rulerAllow;
        this.heirAllow = other.heirAllow;
        this.consortAllow = other.consortAllow;
        this.allow = other.allow;
        this.modifiers = other.modifiers;
    }

    public Condition getRulerAllow() {
        return rulerAllow;
    }

    public Condition getHeirAllow() {
        return heirAllow;
    }

    public Condition getConsortAllow() {
        return consortAllow;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public boolean isMonarchValid(Monarch monarch) {
        if (getAllow() != null && !getAllow().apply(monarch.getCountry(), monarch.getCountry())) {
            return false;
        }

        if (Monarch.class.equals(monarch.getClass()) && (getRulerAllow() == null || getRulerAllow().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        if (Heir.class.equals(monarch.getClass()) && (getHeirAllow() == null || getHeirAllow().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        if (Queen.class.equals(monarch.getClass()) && (getConsortAllow() == null || getConsortAllow().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RulerPersonality)) {
            return false;
        }

        RulerPersonality personality = (RulerPersonality) o;

        return Objects.equals(name, personality.name);
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
