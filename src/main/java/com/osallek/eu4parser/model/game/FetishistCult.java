package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class FetishistCult {

    private final String name;

    private String localizedName;

    private final Modifiers modifiers;

    private final Condition allow;

    private final int sprite;

    public FetishistCult(ClausewitzItem item) {
        this.name = item.getName();
        this.allow = item.getChild("allow") == null ? null : new Condition(item.getChild("allow"));
        this.sprite = item.getVarAsInt("sprite");
        this.modifiers = new Modifiers(item.getVarsNot("sprite"));

        if (this.allow != null) {
            this.allow.removeCondition("has_unlocked_cult", this.name); //Prevent endless recursive
        }
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

    public Modifiers getModifiers() {
        return modifiers;
    }

    public Condition getAllow() {
        return allow;
    }

    public int getSprite() {
        return sprite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FetishistCult)) {
            return false;
        }

        FetishistCult that = (FetishistCult) o;
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
