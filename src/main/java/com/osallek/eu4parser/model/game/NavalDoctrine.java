package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NavalDoctrine {

    private final String name;

    private String localizedName;

    private final Integer buttonGfx;

    private final Condition allow;

    private final Modifiers modifiers;

    public NavalDoctrine(ClausewitzItem item) {
        this.name = item.getName();
        this.buttonGfx = item.getVarAsInt("button_gfx");
        this.modifiers = new Modifiers(item.getChild("country_modifier"));

        ClausewitzItem child = item.getChild("can_select");
        this.allow = child == null ? null : new Condition(child);
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

    public Integer getButtonGfx() {
        return buttonGfx;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NavalDoctrine that = (NavalDoctrine) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
